package com.example.nikonbe.api.client.payment;

import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.orders.repository.OrderRepository;
import com.example.nikonbe.modules.orders.service.interF.OrderService;
import com.example.nikonbe.modules.pos.service.interF.PosService;
import com.example.nikonbe.modules.vnpay.service.interF.VNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  private final OrderService orderService;
  private final VNPayService vnPayService;

  @Value("${api.frontendAdmin.url:http://localhost:3001}")
  private String frontendAdminUrl;

  @Value("${api.frontend.url:http://localhost:3000}")
  private String frontendBaseUrl;

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
      String trackingNumber = params.get("vnp_OrderInfo");
      String orderIdStr = params.get("vnp_TxnRef");

      log.info("Processing VNPay callback - TrackingNumber: {}, OrderId: {}, ResponseCode: {}", trackingNumber, orderIdStr, responseCode);

      if (trackingNumber == null || trackingNumber.isEmpty()) {
        log.error("TrackingNumber is missing in VNPay callback");
        throw new IllegalArgumentException("TrackingNumber không được để trống trong callback");
      }

      Order order = orderRepository.findByTrackingNumber(trackingNumber).orElse(null);
      if (order == null) {
        log.error("Order not found with trackingNumber: {}", trackingNumber);
        throw new IllegalArgumentException("Không tìm thấy đơn hàng với trackingNumber: " + trackingNumber);
      }

      String orderType = order.getOrderType();
      String redirectUrl;

      if ("ONLINE".equalsIgnoreCase(orderType)) {
        if ("00".equals(responseCode)) {
          orderService.completeOnlineOrder(trackingNumber);
          log.info("Online order payment successful: {}. Redirecting to client frontend", trackingNumber);
          redirectUrl =
              frontendBaseUrl.endsWith("/")
                  ? frontendBaseUrl + "checkout/confirmation"
                  : frontendBaseUrl + "/checkout/confirmation";
        } else {
          orderService.handlePaymentFailed(trackingNumber);
          log.warn("Online order payment failed: {}. ResponseCode: {}", trackingNumber, responseCode);
          redirectUrl =
              frontendBaseUrl.endsWith("/")
                  ? frontendBaseUrl + "checkout/payment-failure"
                  : frontendBaseUrl + "/checkout/payment-failure";
        }
      } else {
        String context = "main";
        try {
          if (order.getNote() != null) {
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

        if ("00".equals(responseCode)) {
          redirectUrl = frontendAdminUrl + contextPath + "/pos?payment=success&orderId=" + trackingNumber;
          log.info("POS order payment successful: {}. Redirecting to: {}", trackingNumber, redirectUrl);
        } else {
          redirectUrl =
              frontendAdminUrl
                  + contextPath
                  + "/pos?payment=failed&orderId="
                  + trackingNumber
                  + "&errorCode="
                  + responseCode;
          log.warn("POS order payment failed: {}. ResponseCode: {}", trackingNumber, responseCode);
        }
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
      String redirectUrl = frontendBaseUrl.endsWith("/")
          ? frontendBaseUrl + "checkout/confirmation"
          : frontendBaseUrl + "/checkout/confirmation";
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

  @PostMapping("/success")
  @Operation(
      summary = "Xử lý thanh toán thành công",
      description = "API cập nhật trạng thái đơn hàng khi thanh toán thành công")
  public ResponseEntity<?> handlePaymentSuccess(@RequestParam String trackingNumber) {
    try {
      Order order = orderService.getOrderByTrackingNumber(trackingNumber);
      if (order == null) {
        return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
      }
      
      if (!"ONLINE".equalsIgnoreCase(order.getOrderType())) {
        return ResponseEntity.badRequest().body("API này chỉ dành cho đơn hàng online");
      }
      
      orderService.completeOnlineOrder(trackingNumber);
      log.info("Payment success confirmed for order: {}", trackingNumber);
      return ResponseEntity.ok().body("Đã cập nhật trạng thái đơn hàng thành công");
    } catch (Exception e) {
      log.error("Error handling payment success: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body("Có lỗi xảy ra khi cập nhật trạng thái đơn hàng");
    }
  }

  @PostMapping("/failed")
  @Operation(
      summary = "Xử lý thanh toán thất bại",
      description = "API cập nhật trạng thái đơn hàng khi thanh toán thất bại")
  public ResponseEntity<?> handlePaymentFailed(@RequestParam String trackingNumber) {
    try {
      orderService.handlePaymentFailed(trackingNumber);
      return ResponseEntity.ok().body("Đã cập nhật trạng thái đơn hàng thành công");
    } catch (Exception e) {
      log.error("Error handling payment failed: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body("Có lỗi xảy ra khi cập nhật trạng thái đơn hàng");
    }
  }

  @PostMapping("/retry")
  @Operation(
      summary = "Tạo lại link thanh toán VNPay",
      description = "API tạo lại link thanh toán cho đơn hàng đang chờ thanh toán")
  public ResponseEntity<?> retryPayment(
      @RequestParam String trackingNumber,
      @RequestParam(required = false) String frontendOrigin,
      HttpServletRequest request) {
    try {
      Order order = orderService.getOrderByTrackingNumber(trackingNumber);
      if (order == null) {
        return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
      }

      boolean isVnpay =
          order.getPaymentMethod() != null && order.getPaymentMethod().equalsIgnoreCase("VNPAY");
      boolean isPending =
          order.getStatus() != null && order.getStatus().name().equals("PENDING_PAYMENT");
      boolean inTime =
          order.getCreatedAt() != null
              && order.getCreatedAt().plusMinutes(30).isAfter(java.time.LocalDateTime.now());
      
      if (!isVnpay || !isPending || !inTime) {
        return ResponseEntity.badRequest().body("Không thể thanh toán lại cho đơn này");
      }

      BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;
      BigDecimal finalAmountBd =
          order.getTotalAmount().subtract(discount).add(order.getShippingFee());
      Long finalAmount = finalAmountBd.longValue();
      String ipAddr = request.getRemoteAddr();
      String orderIdStr = order.getId().toString();
      String paymentUrl =
          vnPayService.createPaymentUrl(finalAmount, order.getTrackingNumber(), ipAddr, orderIdStr);

      return ResponseEntity.ok(paymentUrl);
    } catch (Exception e) {
      log.error("Error retrying payment: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body("Có lỗi xảy ra khi tạo lại link thanh toán");
    }
  }
}
