package com.example.nikonbe.modules.orders.dto.response;

import com.example.nikonbe.modules.order_detail.dto.response.OrderDetailReponse;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListOrderResponse {
  private Integer orderId;
  private Integer orderStatus;
  private String orderDate;
  private BigDecimal totalAmount;
  private BigDecimal discount;
  private BigDecimal shippingFee;
  private String paymentMethod;
  private String paymentStatus;
  private String shippingAddress;
  private String note;
  private String trackingNumber;
  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private BigDecimal finalAmount;
  private String orderType;
  private Integer staffId;
  private Integer customerId;
  private List<OrderDetailReponse> orderDetails;
  private String paymentUrl;
}
