package com.example.nikonbe.security.service.auth;

import com.example.nikonbe.common.constants.AuthConstants;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.common.utils.JWTUtil;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.entity.CustomerToken;
import com.example.nikonbe.modules.customer.mapper.TokenMapper;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.customer.repository.CustomerTokenRepository;
import com.example.nikonbe.security.dto.request.LoginRequest;
import com.example.nikonbe.security.dto.response.ErrorResponse;
import com.example.nikonbe.security.dto.response.TokenResponse;
import com.example.nikonbe.security.service.provider.CustomerDetailService;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class CustomerAuthService {

  private final CustomerDetailService customerDetailService;
  private final CustomerRepository customerRepository;
  private final CustomerTokenRepository customerTokenRepository;
  private final TokenMapper tokenMapper;
  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public ResponseEntity<?> login(LoginRequest request) {
    log.info("Customer login attempt for: {}", request.getLogin());

    // Validate input
    ResponseEntity<?> validationError = validateLoginRequest(request);
    if (validationError != null) {
      return validationError;
    }

    try {
      // Authenticate user
      authenticateUser(request);

      // Load user details and generate tokens
      UserDetails userDetails = customerDetailService.loadUserByUsername(request.getLogin());
      Customer customer = findCustomerByUsername(userDetails.getUsername());

      // Generate and save tokens
      TokenResponse tokenResponse = generateAndSaveTokens(customer, userDetails);

      log.info("Customer login successful for: {} (ID: {})", request.getLogin(), customer.getId());
      return ResponseEntity.ok(tokenResponse);

    } catch (BadCredentialsException e) {
      log.warn("Customer login failed - invalid credentials for: {}", request.getLogin());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse(AuthConstants.INVALID_CREDENTIALS));
    } catch (LockedException e) {
      log.warn("Customer login failed - account locked for: {}", request.getLogin());
      return ResponseEntity.status(HttpStatus.LOCKED)
          .body(
              new ErrorResponse("Tài khoản đã bị khóa", Map.of("status", "Tài khoản đã bị khóa")));
    } catch (DisabledException e) {
      log.warn("Customer login failed - account disabled for: {}", request.getLogin());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(AuthConstants.ACCOUNT_LOCKED));
    } catch (UsernameNotFoundException e) {
      log.warn("Customer login failed - user not found: {}", request.getLogin());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ErrorResponse(AuthConstants.ACCOUNT_NOT_FOUND));
    } catch (Exception e) {
      log.error(
          "Unexpected error during customer login for {}: {}",
          request.getLogin(),
          e.getMessage(),
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ErrorResponse(AuthConstants.UNEXPECTED_ERROR));
    }
  }

  private ResponseEntity<?> validateLoginRequest(LoginRequest request) {
    if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(AuthConstants.EMPTY_LOGIN_INFO));
    }

    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(AuthConstants.EMPTY_LOGIN_INFO));
    }

    return null;
  }

  private void authenticateUser(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
  }

  private Customer findCustomerByUsername(String username) {
    return customerRepository
        .findByEmailOrUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(AuthConstants.ACCOUNT_NOT_FOUND));
  }

  private TokenResponse generateAndSaveTokens(Customer customer, UserDetails userDetails) {
    // Remove existing tokens
    customerTokenRepository.deleteByCustomerId(customer.getId());

    // Generate new tokens
    String accessToken = jwtUtil.generateToken(userDetails, customer.getId());
    String refreshToken = jwtUtil.generateRefreshToken(userDetails, customer.getId());

    // Tính thời gian hết hạn refresh token
    LocalDateTime refreshExpiresAt =
        AuthConstants.MODE_TEST_REFRESH
            ? LocalDateTime.now().plusMinutes(AuthConstants.REFRESH_TOKEN_VALIDITY_MINUTES_TEST)
            : LocalDateTime.now().plusDays(AuthConstants.REFRESH_TOKEN_VALIDITY_DAYS);

    // Save tokens
    CustomerToken customerToken =
        CustomerToken.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .customer(customer)
            .expiresAt(refreshExpiresAt)
            .build();

    customerTokenRepository.save(customerToken);
    return tokenMapper.toDto(customerToken);
  }

  public TokenResponse refreshToken(String refreshToken) {
    log.info("Customer refresh token request");

    if (refreshToken == null || refreshToken.trim().isEmpty()) {
      throw new ValidationException("Refresh token không được để trống");
    }

    CustomerToken token =
        customerTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token không tìm thấy"));

    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new ValidationException("Refresh token đã hết hạn");
    }

    UserDetails userDetails =
        customerDetailService.loadUserByUsername(token.getCustomer().getEmail());
    String newAccessToken = jwtUtil.generateToken(userDetails, token.getCustomer().getId());

    // Rotate refresh token
    String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, token.getCustomer().getId());

    token.setAccessToken(newAccessToken);
    token.setRefreshToken(newRefreshToken);
    // Gia hạn thời gian hết hạn refresh token
    if (AuthConstants.MODE_TEST_REFRESH) {
      token.setExpiresAt(
          LocalDateTime.now().plusMinutes(AuthConstants.REFRESH_TOKEN_VALIDITY_MINUTES_TEST));
    } else {
      token.setExpiresAt(LocalDateTime.now().plusDays(AuthConstants.REFRESH_TOKEN_VALIDITY_DAYS));
    }
    CustomerToken saved = customerTokenRepository.save(token);
    log.info("Customer refresh token successful for customer ID: {}", token.getCustomer().getId());
    return tokenMapper.toDto(saved);
  }

  public void logout(Map<String, String> req) {
    String identifier = req.get("identifier");
    log.info("Customer logout request for: {}", identifier);

    if (identifier == null || identifier.trim().isEmpty()) {
      throw new ValidationException("Identifier không được để trống");
    }

    Customer customer = findCustomerByLogin(identifier);
    customerTokenRepository.deleteByCustomerId(customer.getId());
    log.info("Customer logout successful for: {} (ID: {})", identifier, customer.getId());
  }

  private Customer findCustomerByLogin(String login) {
    return customerRepository
        .findByEmailOrUsername(login)
        .orElseThrow(
            () -> new ResourceNotFoundException("Không tìm thấy customer với thông tin: " + login));
  }
}
