package com.example.nikonbe.security.service.mail;

import com.example.nikonbe.common.enums.UserRole;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender javaMailSender;

  @Value("${api.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  @Value("${api.frontendAdmin.url:http://localhost:3001}")
  private String frontendAdminUrl;

  @Value("${mail.from.address:noreply@nikonstore.com}")
  private String fromAddress;

  @Value("${mail.from.name:Nikon Store}")
  private String fromName;

  public void sendPasswordResetEmail(String email, String fullName, String token) {
    try {
      String resetUrl = frontendUrl + "/reset-password?token=" + token;
      String subject = "Đặt lại mật khẩu - Nikon Store";
      String htmlContent = buildPasswordResetEmailContent(fullName, resetUrl, token);
      sendEmail(email, subject, htmlContent);
      log.info("Password reset email sent to: {} with reset URL: {}", email, resetUrl);
    } catch (Exception e) {
      log.error("Failed to send password reset email to {}: {}", email, e.getMessage(), e);
      throw new RuntimeException("Không thể gửi email đặt lại mật khẩu", e);
    }
  }

  public void sendAdminPasswordResetEmail(
      String email, String fullName, String token, UserRole role) {
    try {
      String roleStr = role.name().toLowerCase();
      String resetUrl = frontendAdminUrl + "/" + roleStr + "/reset-password?token=" + token;
      String subject = "Đặt lại mật khẩu Admin - Nikon Store";
      String htmlContent = buildPasswordResetEmailContent(fullName, resetUrl, token);
      sendEmail(email, subject, htmlContent);
      log.info("Admin password reset email sent to: {} with reset URL: {}", email, resetUrl);
    } catch (Exception e) {
      log.error("Failed to send admin password reset email to {}: {}", email, e.getMessage(), e);
      throw new RuntimeException("Không thể gửi email đặt lại mật khẩu", e);
    }
  }

  private void sendEmail(String toEmail, String subject, String htmlContent)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setFrom(fromAddress, fromName);
    helper.setTo(toEmail);
    helper.setSubject(subject);
    helper.setText(htmlContent, true);

    javaMailSender.send(message);
    log.debug("Email sent successfully to: {}", toEmail);
  }

  @Async
  public void sendVoucherAssignedEmail(
      String email,
      String fullName,
      String voucherCode,
      String voucherName,
      BigDecimal discountValue,
      String discountType,
      LocalDateTime endDate) {
    try {
      String subject = "Bạn đã nhận được voucher - Nikon Store";
      String htmlContent =
          buildVoucherAssignedEmailContent(
              fullName, voucherCode, voucherName, discountValue, discountType, endDate);
      sendEmail(email, subject, htmlContent);
      log.info("Voucher assignment email sent to: {} for voucher: {}", email, voucherCode);
    } catch (Exception e) {
      log.error("Failed to send voucher assignment email to {}: {}", email, e.getMessage(), e);
    }
  }

  private String buildPasswordResetEmailContent(String fullName, String resetUrl, String token) {
    return "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
        + "<meta charset='UTF-8'>"
        + "<style>"
        + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
        + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
        + ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }"
        + ".content { padding: 20px; background-color: #f9f9f9; }"
        + ".button { display: inline-block; padding: 12px 30px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }"
        + ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
        + "</style>"
        + "</head>"
        + "<body>"
        + "<div class='container'>"
        + "<div class='header'>"
        + "<h1>Đặt lại mật khẩu</h1>"
        + "</div>"
        + "<div class='content'>"
        + "<p>Xin chào <strong>"
        + fullName
        + "</strong>,</p>"
        + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>"
        + "<p>Vui lòng nhấp vào nút bên dưới để đặt lại mật khẩu:</p>"
        + "<p style='text-align: center;'>"
        + "<a href='"
        + resetUrl
        + "' class='button'>Đặt lại mật khẩu</a>"
        + "</p>"
        + "<p>Hoặc sao chép và dán liên kết sau vào trình duyệt của bạn:</p>"
        + "<p style='word-break: break-all; color: #4CAF50;'>"
        + resetUrl
        + "</p>"
        + "<p><strong>Lưu ý:</strong> Liên kết này sẽ hết hạn sau 30 phút.</p>"
        + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
        + "</div>"
        + "<div class='footer'>"
        + "<p>&copy; 2025 Nikon Store. Tất cả quyền được bảo lưu.</p>"
        + "</div>"
        + "</div>"
        + "</body>"
        + "</html>";
  }

  private String buildVoucherAssignedEmailContent(
      String fullName,
      String voucherCode,
      String voucherName,
      BigDecimal discountValue,
      String discountType,
      LocalDateTime endDate) {
    String formattedDiscountValue;
    String discountTypeDisplay;
    if ("percentage".equals(discountType)) {
      formattedDiscountValue = String.valueOf(discountValue.intValue());
      discountTypeDisplay = "%";
    } else {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
      symbols.setGroupingSeparator(',');
      DecimalFormat formatter = new DecimalFormat("#,###", symbols);
      formattedDiscountValue = formatter.format(discountValue);
      discountTypeDisplay = "đ";
    }

    String expiryDateStr =
        endDate != null
            ? endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            : "Không giới hạn";

    return "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
        + "<meta charset='UTF-8'>"
        + "<style>"
        + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
        + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
        + ".header { background-color: #2563eb; color: white; padding: 20px; text-align: center; }"
        + ".content { padding: 20px; background-color: #f9f9f9; }"
        + ".voucher-box { background-color: #fff; border: 2px dashed #2563eb; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: center; }"
        + ".voucher-code { font-size: 24px; font-weight: bold; color: #2563eb; margin: 10px 0; }"
        + ".discount { font-size: 32px; font-weight: bold; color: #059669; margin: 10px 0; }"
        + ".button { display: inline-block; padding: 12px 30px; background-color: #2563eb; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }"
        + ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }"
        + ".info-row { margin: 10px 0; }"
        + "</style>"
        + "</head>"
        + "<body>"
        + "<div class='container'>"
        + "<div class='header'>"
        + "<h1>Bạn đã nhận được voucher!</h1>"
        + "</div>"
        + "<div class='content'>"
        + "<p>Xin chào <strong>"
        + fullName
        + "</strong>,</p>"
        + "<p>Chúc mừng! Bạn đã nhận được một voucher từ Nikon Store.</p>"
        + "<div class='voucher-box'>"
        + "<div class='voucher-code'>Mã voucher: "
        + voucherCode
        + "</div>"
        + "<div style='margin: 10px 0; color: #666;'>"
        + voucherName
        + "</div>"
        + "<div class='discount'>Giảm "
        + formattedDiscountValue
        + discountTypeDisplay
        + "</div>"
        + "</div>"
        + "<div class='info-row'><strong>Mã voucher:</strong> "
        + voucherCode
        + "</div>"
        + "<div class='info-row'><strong>Mô tả:</strong> "
        + voucherName
        + "</div>"
        + "<div class='info-row'><strong>Giá trị giảm:</strong> "
        + formattedDiscountValue
        + discountTypeDisplay
        + "</div>"
        + "<div class='info-row'><strong>Hạn sử dụng:</strong> "
        + expiryDateStr
        + "</div>"
        + "<p style='text-align: center; margin-top: 30px;'>"
        + "<a href='"
        + frontendUrl
        + "' class='button'>Sử dụng voucher ngay</a>"
        + "</p>"
        + "<p style='margin-top: 20px;'>Vui lòng sử dụng mã voucher này khi thanh toán để nhận được ưu đãi.</p>"
        + "</div>"
        + "<div class='footer'>"
        + "<p>&copy; 2025 Nikon Store. Tất cả quyền được bảo lưu.</p>"
        + "</div>"
        + "</div>"
        + "</body>"
        + "</html>";
  }
}
