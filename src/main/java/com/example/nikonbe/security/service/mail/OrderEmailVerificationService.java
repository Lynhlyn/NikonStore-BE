package com.example.nikonbe.security.service.mail;

import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEmailVerificationService {

  private final EmailService emailService;

  private final ConcurrentHashMap<String, String> verifiedEmails = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, String> emailToTokenMap = new ConcurrentHashMap<>();

  public String sendOrderVerificationEmail(String email, String customerName) {
    String token = String.format("%06d", (int) (Math.random() * 1000000));

    emailToTokenMap.put(email, token);

    emailService.sendAuthenticationCodeEmail(email, customerName, token);

    return token;
  }

  public boolean verifyOrderEmail(String token, String email) {
    if (token == null || token.trim().isEmpty()) {
      throw new IllegalArgumentException("Token không được để trống");
    }

    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email không được để trống");
    }

    String storedToken = emailToTokenMap.get(email);

    if (storedToken == null) {
      throw new IllegalArgumentException(
          "Email chưa được gửi mã xác thực hoặc mã đã hết hạn. Vui lòng yêu cầu gửi lại mã.");
    }

    if (storedToken.equals(token)) {
      verifiedEmails.put(email, token);

      emailToTokenMap.remove(email);

      return true;
    }

    return false;
  }

  public boolean isEmailVerified(String email) {
    return verifiedEmails.containsKey(email);
  }
}

