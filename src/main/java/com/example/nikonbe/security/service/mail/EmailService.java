package com.example.nikonbe.security.service.mail;

import com.example.nikonbe.common.enums.EmailAction;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.email.template.service.interF.TemplateEmailService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final TemplateEmailService templateEmailService;

  @Value("${api.frontend.url}")
  private String frontendUrl;

  @Value("${api.frontendAdmin.url}")
  private String frontendAdminUrl;

  @Async
  public void sendOrderPendingConfirmationEmail(
      String email, String customerName, String orderNumber, BigDecimal orderTotal) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("orderTotal", orderTotal);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(
          EmailAction.ORDER_PENDING_CONFIRMATION, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng chờ xác nhận", e);
    }
  }

  @Async
  public void sendOrderConfirmedEmail(
      String email, String customerName, String orderNumber, BigDecimal orderTotal) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("orderTotal", orderTotal);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.ORDER_CONFIRMED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng đã xác nhận", e);
    }
  }

  @Async
  public void sendOrderPreparingEmail(
      String email, String customerName, String orderNumber, BigDecimal orderTotal) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("orderTotal", orderTotal);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.ORDER_PREPARING, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng đang chuẩn bị", e);
    }
  }

  @Async
  public void sendOrderShippingEmail(
      String email, String customerName, String orderNumber, BigDecimal orderTotal) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("orderTotal", orderTotal);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.ORDER_SHIPPING, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng đang giao", e);
    }
  }

  @Async
  public void sendOrderCompletedEmail(
      String email, String customerName, String orderNumber, BigDecimal orderTotal) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("orderTotal", orderTotal);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.ORDER_COMPLETED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng hoàn thành", e);
    }
  }

  @Async
  public void sendOrderCancelledEmail(
      String email, String customerName, String orderNumber, String reason) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("reason", reason);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.ORDER_CANCELLED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng đã hủy", e);
    }
  }

  @Async
  public void sendOrderPendingPaymentEmail(
      String email,
      String customerName,
      String orderNumber,
      BigDecimal orderTotal,
      String paymentUrl) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("orderTotal", orderTotal);
      templateData.put("paymentUrl", paymentUrl);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(
          EmailAction.ORDER_PENDING_PAYMENT, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng chờ thanh toán", e);
    }
  }

  @Async
  public void sendOrderFailedDeliveryEmail(
      String email, String customerName, String orderNumber, String reason) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("orderNumber", orderNumber);
      templateData.put("reason", reason);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(
          EmailAction.ORDER_FAILED_DELIVERY, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đơn hàng giao thất bại", e);
    }
  }

  @Async
  public void sendVoucherAssignedEmail(
      String email,
      String customerName,
      String voucherCode,
      String description,
      BigDecimal discountValue,
      String discountType,
      LocalDateTime endDate) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", customerName);
      templateData.put("customerName", customerName);
      templateData.put("voucherCode", voucherCode);
      templateData.put("description", description);
      templateData.put("discountValue", discountValue);
      templateData.put("discountType", discountType);
      templateData.put("endDate", endDate);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.VOUCHER_ASSIGNED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo voucher được gán", e);
    }
  }

  @Async
  public void sendAdminPasswordResetEmail(
      String email, String fullName, String resetToken, UserRole role) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", fullName);
      templateData.put("fullName", fullName);
      templateData.put("resetToken", resetToken);
      templateData.put("role", role != null ? role.name() : "");
      templateData.put("frontendAdminUrl", frontendAdminUrl);

      sendTemplateEmailWithFallback(
          email, templateData, EmailAction.ADMIN_FORGOT_PASSWORD, EmailAction.RESET_PASSWORD);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email đặt lại mật khẩu admin", e);
    }
  }

  @Async
  public void sendRegisterSuccessEmail(String email, String fullName) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", fullName);
      templateData.put("fullName", fullName);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.REGISTER_SUCCESS, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email chào mừng", e);
    }
  }

  @Async
  public void sendForgotPasswordEmail(String email, String fullName, String resetToken) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", fullName);
      templateData.put("fullName", fullName);
      templateData.put("resetToken", resetToken);
      templateData.put("frontendUrl", frontendUrl);

      sendTemplateEmailWithFallback(
          email, templateData, EmailAction.CLIENT_FORGOT_PASSWORD, EmailAction.FORGOT_PASSWORD);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email quên mật khẩu", e);
    }
  }

  @Async
  public void sendPasswordChangedEmail(String email, String fullName) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", fullName);
      templateData.put("fullName", fullName);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.PASSWORD_CHANGED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo đổi mật khẩu", e);
    }
  }

  @Async
  public void sendVerifyEmail(String email, String fullName, String verificationToken) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", fullName);
      templateData.put("fullName", fullName);
      templateData.put("verificationToken", verificationToken);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.VERIFY_EMAIL, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email xác thực email", e);
    }
  }

  @Async
  public void sendWelcomeEmail(String email, String fullName) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("name", fullName);
      templateData.put("fullName", fullName);
      templateData.put("frontendUrl", frontendUrl);

      templateEmailService.sendTemplateEmail(EmailAction.WELCOME, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email chào mừng", e);
    }
  }

  @Async
  public void sendAccountLockedEmail(String email, String fullName, String reason) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("fullName", fullName);
      templateData.put("email", email);
      templateData.put("reason", reason);

      templateEmailService.sendTemplateEmail(EmailAction.ACCOUNT_LOCKED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo khóa tài khoản", e);
    }
  }

  @Async
  public void sendAccountUnlockedEmail(String email, String fullName) {
    try {
      Map<String, Object> templateData = new HashMap<>();
      templateData.put("fullName", fullName);
      templateData.put("email", email);

      templateEmailService.sendTemplateEmail(EmailAction.ACCOUNT_UNLOCKED, email, templateData);

    } catch (Exception e) {
      throw new RuntimeException("Không thể gửi email thông báo mở khoá tài khoản", e);
    }
  }

  private void sendTemplateEmailWithFallback(
      String email,
      Map<String, Object> templateData,
      EmailAction primary,
      EmailAction... fallback) {
    try {
      templateEmailService.sendTemplateEmail(primary, email, templateData);
    } catch (ResourceNotFoundException notFound) {
      if (fallback == null || fallback.length == 0) {
        throw notFound;
      }
      for (EmailAction candidate : fallback) {
        try {
          templateEmailService.sendTemplateEmail(candidate, email, templateData);
          return;
        } catch (ResourceNotFoundException ignored) {
        }
      }
      throw notFound;
    }
  }
}
