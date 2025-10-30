package com.example.nikonbe.modules.product_detail.dto.request;

import com.example.nikonbe.common.enums.Status;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailUpdateDTO {
  private String sku;
  private Integer stock;
  private Integer reservedStock;
  private Integer productId;
  private Integer colorId;
  private Integer capacityId;
  private BigDecimal price;
  private Status status;
}
