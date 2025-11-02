package com.example.nikonbe.security.service.auth;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.entity.StaffToken;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import com.example.nikonbe.modules.staff.repository.StaffTokenRepository;
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
public class StaffPasswordService {

  private final StaffRepository staffRepository;
  private final StaffTokenRepository staffTokenRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  public void createPasswordResetToken(String email, String role) {
    log.info("Creating password reset token for staff email: {} with role: {}", email, role);

    if (email == null || email.trim().isEmpty()) {
      throw new ValidationException("Email không được để trống");
    }

    Optional<Staff> staffOpt = staffRepository.findByEmail(email.trim());

    if (staffOpt.isEmpty()) {
      throw new ResourceNotFoundException("Không tìm thấy nhân viên với email: " + email);
    }

    Staff staff = staffOpt.get();

    if (staff.getStatus() != Status.ACTIVE) {
      throw new ValidationException("Tài khoản nhân viên không hoạt động");
    }

    UserRole emailRole;
    if (role != null && !role.trim().isEmpty()) {
      try {
        emailRole = UserRole.valueOf(role.trim());
        log.info("Using role from request: {}", emailRole);
      } catch (IllegalArgumentException e) {
        log.warn(
            "Invalid role from request: {}, falling back to staff role: {}", role, staff.getRole());
        emailRole = staff.getRole();
      }
    } else {
      emailRole = staff.getRole();
      log.info("Using staff role from database: {}", emailRole);
    }

    String resetToken = generateSecureToken();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

    Optional<StaffToken> tokenOpt = staffTokenRepository.findByStaffId(staff.getId());

    if (tokenOpt.isPresent()) {
      StaffToken existingToken = tokenOpt.get();
      existingToken.setTokenReset(resetToken);
      existingToken.setExpiresAt(expiresAt);
      existingToken.setUpdatedAt(LocalDateTime.now());
      staffTokenRepository.save(existingToken);
    } else {
      StaffToken staffToken =
          StaffToken.builder()
              .staff(staff)
              .accessToken(generateSecureToken())
              .refreshToken(generateSecureToken())
              .tokenReset(resetToken)
              .expiresAt(expiresAt)
              .build();

      staffTokenRepository.save(staffToken);
    }

    try {
      emailService.sendAdminPasswordResetEmail(
          staff.getEmail(), staff.getFullName(), resetToken, emailRole);
      log.info(
          "Password reset token created and email sent for staff: {} with role: {}",
          email,
          emailRole);
    } catch (Exception e) {
      log.error(
          "Failed to send password reset email for staff: {} with role: {}", email, emailRole, e);
      throw new ValidationException("Không thể gửi email đặt lại mật khẩu");
    }
  }

  public void resetPassword(String token, String newPassword) {
    log.info("Resetting password with token");

    if (token == null || token.trim().isEmpty()) {
      throw new ValidationException("Token không được để trống");
    }

    if (newPassword == null || newPassword.trim().isEmpty()) {
      throw new ValidationException("Mật khẩu mới không được để trống");
    }

    if (newPassword.length() < 6) {
      throw new ValidationException("Mật khẩu mới phải có ít nhất 6 ký tự");
    }

    LocalDateTime currentTime = LocalDateTime.now();
    Optional<StaffToken> tokenOpt = staffTokenRepository.findValidResetToken(token, currentTime);

    if (tokenOpt.isEmpty()) {
      throw new ValidationException("Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn");
    }

    StaffToken staffToken = tokenOpt.get();
    Staff staff =
        staffRepository
            .findById(staffToken.getStaff().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

    if (staff.getStatus() != Status.ACTIVE) {
      throw new ValidationException("Tài khoản không hoạt động");
    }

    staff.setPassword(passwordEncoder.encode(newPassword));
    staff.setUpdatedAt(LocalDateTime.now());
    staffRepository.save(staff);

    staffTokenRepository.clearResetToken(staff.getId());

    staffTokenRepository.deleteByStaffId(staff.getId());

    log.info("Password reset successful for staff ID: {}", staff.getId());
  }

  public boolean validateResetToken(String token) {
    if (token == null || token.trim().isEmpty()) {
      return false;
    }

    LocalDateTime currentTime = LocalDateTime.now();
    Optional<StaffToken> tokenOpt = staffTokenRepository.findValidResetToken(token, currentTime);
    boolean isValid = tokenOpt.isPresent();

    log.debug("Reset token validation result: {}", isValid);
    return isValid;
  }

  private String generateSecureToken() {
    return UUID.randomUUID().toString().replace("-", "")
        + Long.toHexString(System.currentTimeMillis());
  }

  @Scheduled(fixedRate = 3600000)
  public void cleanupExpiredResetTokens() {
    log.debug("Cleaning up expired staff reset tokens");
    try {
      staffTokenRepository.clearExpiredResetTokens(LocalDateTime.now());
    } catch (Exception e) {
      log.error("Error cleaning up expired reset tokens", e);
    }
  }
}
