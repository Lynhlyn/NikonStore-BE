package com.example.nikonbe.modules.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCartItemRequest {
  private Integer customerId;

  @NotNull(message = "Mã sản phẩm không được để trống")
  private Integer productId;

  private String cookieId;
}
