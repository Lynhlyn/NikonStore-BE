package com.example.nikonbe.modules.cart_detail.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
  private Integer cartDetailId;
  private Integer productDetailId;
  private String productName;
  private String sku;
  private String color;
  private String capacity;
  private String imageUrl;
  private Integer quantity;
  private BigDecimal price;
  private BigDecimal discount;
  private BigDecimal totalPrice;
  private Integer stock;
}
