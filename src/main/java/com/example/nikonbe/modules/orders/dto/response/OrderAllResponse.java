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
public class OrderAllResponse {
  private Integer orderid;
  private String trackingNumber;
  private Integer orderStatus;
  private Integer customerId;
  private String customerName;
  private String customerPhone;
  private String customerEmail;
  private LocalDateTime orderDate;
  private BigDecimal totalAmount;
  private BigDecimal discount;
  private BigDecimal shippingFee;
  private String ordertype;
  private String paymentMethod;
  private String paymentStatus;
  private String shippingAddress;
  private String notes;
  private String recipientPhone;
  private String recipientEmail;
  private String voucherCode;
  private String staffName;
}
