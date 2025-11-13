package com.example.nikonbe.modules.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusResponse {
  private String trackingNumber;
  private Integer status;
  private String paymentStatus;
}
