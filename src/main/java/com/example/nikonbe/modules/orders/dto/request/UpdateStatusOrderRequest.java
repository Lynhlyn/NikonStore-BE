package com.example.nikonbe.modules.orders.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusOrderRequest {
  private Integer staffId;
  private Integer orderId;
  private Integer afterStatus;
  private String reason;
}
