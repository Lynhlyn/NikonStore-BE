package com.example.nikonbe.modules.product_detail.dto.response;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailWithImageResponseDTO {
  private Integer id;
  private String sku;
  private Integer stock;
  private Integer reservedStock;
  private Integer availableStock;
  private String productName;
  private ColorResponseDTO color;
  private CapacityResponseDTO capacity;
  private BigDecimal price;
  private Status status;
  private PromotionResponseDTO promotion;
  private String thumbnailImage;
  private BigDecimal discountPrice;
  private BigDecimal discountAmount;
}
