package com.example.nikonbe.modules.cart.dto.response;

import com.example.nikonbe.modules.cart_detail.dto.response.CartItemResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
  private Integer cartId;
  private Integer customerId;
  private String cookieId;
  private List<CartItemResponse> items;
}
