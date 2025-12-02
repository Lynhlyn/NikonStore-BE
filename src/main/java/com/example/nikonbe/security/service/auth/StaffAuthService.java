package com.example.nikonbe.security.service.auth;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.UnauthorizedException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.common.utils.JWTUtil;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.entity.StaffToken;
import com.example.nikonbe.modules.staff.mapper.StaffTokenMapper;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import com.example.nikonbe.modules.staff.repository.StaffTokenRepository;
import com.example.nikonbe.security.dto.request.AdminLoginRequest;
import com.example.nikonbe.security.dto.response.AdminLoginResponse;
import com.example.nikonbe.security.dto.response.CurrentUserResponse;
import com.example.nikonbe.security.dto.response.TokenResponse;
import com.example.nikonbe.security.service.provider.StaffDetailService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class StaffAuthService {

  private final StaffDetailService staffDetailService;
  private final StaffRepository staffRepository;
  private final StaffTokenRepository staffTokenRepository;
  private final StaffTokenMapper staffTokenMapper;
  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  public StaffAuthService(
      StaffDetailService staffDetailService,
      StaffRepository staffRepository,
      StaffTokenRepository staffTokenRepository,
      StaffTokenMapper staffTokenMapper,
      JWTUtil jwtUtil,
      PasswordEncoder passwordEncoder) {
    this.staffDetailService = staffDetailService;
    this.staffRepository = staffRepository;
    this.staffTokenRepository = staffTokenRepository;
    this.staffTokenMapper = staffTokenMapper;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
  }

  public AdminLoginResponse adminLogin(AdminLoginRequest request) {
    log.info("Admin login attempt for email: {}", request.getEmail());

    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      throw new ValidationException("Email không được để trống");
    }

    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new ValidationException("Mật khẩu không được để trống");
    }

    if (request.getRole() == null || request.getRole().trim().isEmpty()) {
      throw new ValidationException("Vai trò không được để trống");
    }

    try {
      Staff staff =
          staffRepository
              .findByEmail(request.getEmail().trim())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Không tìm thấy tài khoản với email: " + request.getEmail()));

      if (staff.getStatus() != Status.ACTIVE) {
        throw new ValidationException("Tài khoản không hoạt động");
      }

      UserRole requestedRole;
      try {
        requestedRole = UserRole.valueOf(request.getRole().trim().toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new ValidationException("Vai trò không hợp lệ");
      }

      if (!staff.getRole().equals(requestedRole)) {
        throw new ValidationException("Vai trò không khớp");
      }

      if (!passwordEncoder.matches(request.getPassword(), staff.getPassword())) {
        throw new ValidationException("Mật khẩu không chính xác");
      }

      staffTokenRepository.deleteByStaffId(staff.getId());

      UserDetails userDetails = staffDetailService.loadUserByUsername(staff.getEmail());

      String accessToken = jwtUtil.generateStaffToken(userDetails, staff.getId());
      String refreshToken = jwtUtil.generateStaffRefreshToken(userDetails, staff.getId());

      StaffToken staffToken =
          StaffToken.builder()
              .staff(staff)
              .accessToken(accessToken)
              .refreshToken(refreshToken)
              .expiresAt(LocalDateTime.now().plusDays(30))
              .build();

      staffTokenRepository.save(staffToken);

      AdminLoginResponse response = new AdminLoginResponse();
      response.setId(staff.getId());
      response.setEmail(staff.getEmail());
      response.setFullName(staff.getFullName());
      response.setRole(staff.getRole().name());
      response.setAccessToken(accessToken);
      response.setRefreshToken(refreshToken);

      log.info("Admin login successful for: {} (ID: {})", request.getEmail(), staff.getId());
      return response;

    } catch (ValidationException | ResourceNotFoundException e) {
      log.warn("Admin login failed for {}: {}", request.getEmail(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(
          "Unexpected error during admin login for {}: {}", request.getEmail(), e.getMessage(), e);
      throw new ValidationException("Đã xảy ra lỗi hệ thống");
    }
  }

  public TokenResponse refreshToken(String refreshToken) {
    log.info("Staff refresh token request");

    if (refreshToken == null || refreshToken.trim().isEmpty()) {
      throw new ValidationException("Refresh token không được để trống");
    }

    StaffToken token =
        staffTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token không tìm thấy"));

    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new ValidationException("Refresh token đã hết hạn");
    }

    UserDetails userDetails = staffDetailService.loadUserByUsername(token.getStaff().getUsername());
    String newAccessToken = jwtUtil.generateStaffToken(userDetails, token.getStaff().getId());

    token.setAccessToken(newAccessToken);
    token.setUpdatedAt(LocalDateTime.now());
    StaffToken updatedToken = staffTokenRepository.save(token);

    log.info("Staff refresh token successful for staff ID: {}", token.getStaff().getId());
    return staffTokenMapper.toDto(updatedToken);
  }

  public void logout(Map<String, String> request) {
    String identifier = request.get("identifier");
    log.info("Staff logout request for: {}", identifier);

    if (identifier == null || identifier.trim().isEmpty()) {
      throw new ValidationException("Identifier không được để trống");
    }

    Staff staff = findStaffByLogin(identifier);

    staffTokenRepository.deleteByStaffId(staff.getId());
    log.info("Staff logout successful for: {} (ID: {})", identifier, staff.getId());
  }

  private Staff findStaffByLogin(String login) {
    Optional<Staff> staffOpt = staffRepository.findByUsername(login);

    if (staffOpt.isEmpty()) {
      staffOpt = staffRepository.findByEmail(login);
    }

    return staffOpt.orElseThrow(
        () -> new ResourceNotFoundException("Không tìm thấy staff với thông tin: " + login));
  }

  public CurrentUserResponse getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException("Người dùng chưa đăng nhập hoặc token không hợp lệ");
    }
    if (authentication.getName() == null) {
      throw new ValidationException("Thông tin xác thực không hợp lệ");
    }

    String username = authentication.getName();
    log.debug("Getting current user info for username: {}", username);

    Staff staff = findStaffByLogin(username);

    if (staff.getStatus() != Status.ACTIVE) {
      throw new ValidationException("Tài khoản không hoạt động");
    }

    CurrentUserResponse response = new CurrentUserResponse();
    response.setId(staff.getId());
    response.setEmail(staff.getEmail());
    response.setName(staff.getFullName());
    response.setRole(staff.getRole().name());
    response.setPhoneNumber(staff.getPhoneNumber());
    response.setStatus(staff.getStatus().name());

    log.info("Successfully retrieved current user info for staff ID: {}", staff.getId());
    return response;
  }
}
