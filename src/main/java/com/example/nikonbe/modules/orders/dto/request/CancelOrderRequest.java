package com.example.nikonbe.modules.orders.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderRequest {
  @NotNull(message = "ID đơn hàng không được để trống")
  private Integer orderId;

  private Integer customerId;
  private Integer staffId;
  private Integer status;
  private String reason;
}
