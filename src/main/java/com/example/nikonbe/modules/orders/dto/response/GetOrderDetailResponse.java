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
public class GetOrderDetailResponse {
  private Integer orderId;
  private String trackingNumber;
  private Integer customerId;
  private Integer orderStatus;
  private String orderDate;
  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private String shippingAddress;
  private BigDecimal totalAmount;
  private BigDecimal discount;
  private BigDecimal shippingFee;
  private String paymentMethod;
  private String orderType;
  private String paymentStatus;
  private String note;
  private List<OrderDetailReponse> orderDetails;
}
