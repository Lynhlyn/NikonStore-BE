package com.example.nikonbe.modules.product_detail.dto.response;

import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailListingResponseDTO {
  private Integer variantId;
  private String sku;
  private Integer stock;
  private Integer reservedStock;
  private Integer availableStock;

  private ColorResponseDTO color;
  private CapacityResponseDTO capacity;

  private BigDecimal originalPrice;
  private BigDecimal discountPrice;
  private BigDecimal finalPrice;

  private Integer promotionId;
  private String promotionName;
  private String promotionType;
  private BigDecimal promotionValue;
  private BigDecimal discountAmount;

  private String thumbnailImage;

  private Boolean isPrimary;
  private Integer sortOrder;
}
