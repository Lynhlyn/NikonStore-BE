package com.example.nikonbe.modules.orders.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
  private Integer orderId;
  private String trackingNumber;
  private Integer orderStatus;
  private LocalDateTime orderDate;
  private BigDecimal totalAmount;
  private BigDecimal discount;
  private BigDecimal shippingFee;
  private String paymentMethod;
  private String note;
  private Long remainingPaymentTime;
}
