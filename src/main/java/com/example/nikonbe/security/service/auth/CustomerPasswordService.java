package com.example.nikonbe.security.service.auth;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.entity.CustomerToken;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.customer.repository.CustomerTokenRepository;
import com.example.nikonbe.security.service.mail.EmailService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerPasswordService {

  private final CustomerRepository customerRepository;
  private final CustomerTokenRepository customerTokenRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  public void requestPasswordReset(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new ValidationException("Email không được để trống");
    }

    String normalizedEmail = email.trim();
    Customer customer =
        customerRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Không tìm thấy khách hàng với email: " + normalizedEmail));

    if (customer.getStatus() != Status.ACTIVE) {
      throw new ValidationException("Tài khoản khách hàng không hoạt động");
    }

    String resetToken = generateSecureToken();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
    LocalDateTime now = LocalDateTime.now();

    Optional<CustomerToken> tokenOpt = customerTokenRepository.findByCustomerId(customer.getId());
    if (tokenOpt.isPresent()) {
      customerTokenRepository.updateResetToken(customer.getId(), resetToken, expiresAt, now);
    } else {
      CustomerToken token =
          CustomerToken.builder()
              .customer(customer)
              .accessToken(generateSecureToken())
              .refreshToken(generateSecureToken())
              .tokenReset(resetToken)
              .expiresAt(expiresAt)
              .build();
      customerTokenRepository.save(token);
    }

    try {
      emailService.sendForgotPasswordEmail(customer.getEmail(), customer.getFullName(), resetToken);
    } catch (Exception e) {
      log.error("Failed to send customer password reset email for {}", normalizedEmail, e);
      throw new ValidationException("Không thể gửi email đặt lại mật khẩu");
    }
  }

  public void resetPassword(String token, String newPassword) {
    if (token == null || token.trim().isEmpty()) {
      throw new ValidationException("Token không được để trống");
    }

    if (newPassword == null || newPassword.trim().isEmpty()) {
      throw new ValidationException("Mật khẩu mới không được để trống");
    }

    if (newPassword.length() < 6) {
      throw new ValidationException("Mật khẩu mới phải có ít nhất 6 ký tự");
    }

    LocalDateTime now = LocalDateTime.now();
    CustomerToken customerToken =
        customerTokenRepository
            .findValidResetToken(token.trim(), now)
            .orElseThrow(
                () ->
                    new ValidationException("Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn"));

    Customer customer =
        customerRepository
            .findById(customerToken.getCustomer().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

    if (customer.getStatus() != Status.ACTIVE) {
      throw new ValidationException("Tài khoản không hoạt động");
    }

    customer.setPassword(passwordEncoder.encode(newPassword));
    customer.setUpdatedAt(now);
    customerRepository.save(customer);

    customerTokenRepository.clearResetToken(customer.getId());
    customerTokenRepository.deleteByCustomerId(customer.getId());

    try {
      emailService.sendPasswordChangedEmail(customer.getEmail(), customer.getFullName());
    } catch (Exception e) {
      log.error("Failed to send customer password changed email for {}", customer.getEmail(), e);
    }
  }

  public boolean validateResetToken(String token) {
    if (token == null || token.trim().isEmpty()) {
      return false;
    }
    return customerTokenRepository
        .findValidResetToken(token.trim(), LocalDateTime.now())
        .isPresent();
  }

  private String generateSecureToken() {
    return UUID.randomUUID().toString().replace("-", "")
        + Long.toHexString(System.currentTimeMillis());
  }

  @Scheduled(fixedRate = 3600000)
  public void cleanupExpiredResetTokens() {
    try {
      customerTokenRepository.clearExpiredResetTokens(LocalDateTime.now());
    } catch (Exception e) {
      log.error("Error cleaning up expired customer reset tokens", e);
    }
  }
}
