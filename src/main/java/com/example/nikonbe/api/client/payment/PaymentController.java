package com.example.nikonbe.api.client.payment;

import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.orders.repository.OrderRepository;
import com.example.nikonbe.modules.pos.service.interF.PosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${api.version}/payment")
@RequiredArgsConstructor
@Tag(name = "Payment Callback", description = "API xử lý callback từ payment gateway")
public class PaymentController {

  private final PosService posService;
  private final OrderRepository orderRepository;

  @Value("${api.frontendAdmin.url:http://localhost:3001}")
  private String frontendAdminUrl;

  @GetMapping("/return")
  @PostMapping("/return")
  @Operation(
      summary = "Callback từ VNPAY sau khi thanh toán",
      description = "API nhận callback từ VNPAY và xử lý kết quả thanh toán")
  public org.springframework.http.ResponseEntity<String> vnpayReturn(
      HttpServletRequest request, @RequestParam Map<String, String> params) {
    try {
      log.info("Received VNPay callback with params: {}", params);

      Map<String, String> vnpParams = new HashMap<>(params);

      String responseCode = params.get("vnp_ResponseCode");
      String orderId = params.get("vnp_TxnRef");

      log.info("Processing VNPay callback - Order: {}, ResponseCode: {}", orderId, responseCode);

      String context = "main";
      try {
        Order order = orderRepository.findByTrackingNumber(orderId).orElse(null);
        if (order != null && order.getNote() != null) {
          String note = order.getNote();
          String paymentContextMarker = "PAYMENT_CONTEXT:";
          int startIndex = note.indexOf(paymentContextMarker);
          if (startIndex != -1) {
            int contextStart = startIndex + paymentContextMarker.length();
            int contextEnd = note.indexOf("|", contextStart);
            if (contextEnd == -1) {
              contextEnd = note.length();
            }
            String extractedContext = note.substring(contextStart, contextEnd).trim();
            if (extractedContext.equals("main") || extractedContext.equals("staff")) {
              context = extractedContext;
            }
          }
        }
      } catch (Exception e) {
        log.warn("Error extracting context from order note: {}", e.getMessage());
      }

      posService.handleVnpayCallback(vnpParams);

      String contextPath = context.equals("staff") ? "/staff" : "/main";

      String redirectUrl;
      if ("00".equals(responseCode)) {
        redirectUrl = frontendAdminUrl + contextPath + "/pos?payment=success&orderId=" + orderId;
        log.info("Payment successful for order: {}. Redirecting to: {}", orderId, redirectUrl);
      } else {
        redirectUrl =
            frontendAdminUrl
                + contextPath
                + "/pos?payment=failed&orderId="
                + orderId
                + "&errorCode="
                + responseCode;
        log.warn("Payment failed for order: {}. ResponseCode: {}", orderId, responseCode);
      }

      String htmlContent =
          "<!DOCTYPE html>"
              + "<html>"
              + "<head>"
              + "<meta charset=\"UTF-8\">"
              + "<meta http-equiv=\"refresh\" content=\"0;url="
              + redirectUrl
              + "\">"
              + "<script>"
              + "window.location.href = '"
              + redirectUrl
              + "';"
              + "</script>"
              + "<title>Redirecting...</title>"
              + "</head>"
              + "<body>"
              + "<p>Đang chuyển hướng... Nếu không tự động chuyển, <a href=\""
              + redirectUrl
              + "\">click vào đây</a>.</p>"
              + "</body>"
              + "</html>";

      return org.springframework.http.ResponseEntity.ok()
          .contentType(MediaType.TEXT_HTML)
          .body(htmlContent);
    } catch (Exception e) {
      log.error("Error handling VNPay callback: {}", e.getMessage(), e);
      String redirectUrl = frontendAdminUrl + "/main/pos?payment=error";
      String htmlContent =
          "<!DOCTYPE html>"
              + "<html>"
              + "<head>"
              + "<meta charset=\"UTF-8\">"
              + "<meta http-equiv=\"refresh\" content=\"0;url="
              + redirectUrl
              + "\">"
              + "<script>"
              + "window.location.href = '"
              + redirectUrl
              + "';"
              + "</script>"
              + "<title>Redirecting...</title>"
              + "</head>"
              + "<body>"
              + "<p>Đang chuyển hướng... Nếu không tự động chuyển, <a href=\""
              + redirectUrl
              + "\">click vào đây</a>.</p>"
              + "</body>"
              + "</html>";

      return org.springframework.http.ResponseEntity.ok()
          .contentType(MediaType.TEXT_HTML)
          .body(htmlContent);
    }
  }
}
