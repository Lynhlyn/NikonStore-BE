package com.example.nikonbe.security.service.mail;

import com.example.nikonbe.common.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

  @Value("${api.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  @Value("${api.frontendAdmin.url:http://localhost:3001}")
  private String frontendAdminUrl;

  public void sendPasswordResetEmail(String email, String fullName, String token) {
    try {
      String resetUrl = frontendUrl + "/reset-password?token=" + token;
      log.info("Password reset email would be sent to: {} with reset URL: {}", email, resetUrl);
    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email đặt lại mật khẩu", e);
    }
  }

  public void sendAdminPasswordResetEmail(
      String email, String fullName, String token, UserRole role) {
    try {
      String roleStr = role.name().toLowerCase();
      String resetUrl = frontendAdminUrl + "/" + roleStr + "/reset-password?token=" + token;
      log.info(
          "Admin password reset email would be sent to: {} with reset URL: {}", email, resetUrl);
    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email đặt lại mật khẩu", e);
    }
  }
}
