package com.example.nikonbe.modules.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartItemRequest {
  @NotNull(message = "Mã sản phẩm không được để trống")
  private Integer productId;

  private Integer customerId;

  @NotNull(message = "Số lượng không được để trống")
  @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
  private Integer quantity;

  private String cookieId;
}
