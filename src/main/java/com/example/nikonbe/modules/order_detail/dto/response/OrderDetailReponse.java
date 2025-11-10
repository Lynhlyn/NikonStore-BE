package com.example.nikonbe.modules.order_detail.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailReponse {
  private Integer orderDetailId;
  private String sku;
  private Integer quantity;
  private String productName;
  private String brandName;
  private String categoryName;
  private String colorName;
  private String capacityName;
  private BigDecimal price;
  private String dimensions;
  private String compartment;
  private String strapTypeName;
  private String imageUrl;
}
