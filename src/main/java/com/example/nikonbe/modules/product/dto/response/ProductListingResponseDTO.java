package com.example.nikonbe.modules.product.dto.response;

import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailListingResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListingResponseDTO {
  private Integer productId;
  private String productName;
  private String description;
  private String dimensions;
  private Double weight;
  private String waterproofRating;

  private BrandResponseDTO brand;
  private StrapTypeResponseDTO strapType;
  private MaterialResponseDTO material;
  private CategoryResponseDTO category;
  private List<String> tags;
  private List<String> features;

  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private BigDecimal bestDiscountPrice;

  private Integer bestPromotionId;
  private String bestPromotionName;
  private String bestPromotionType;
  private BigDecimal bestPromotionValue;

  private List<ProductDetailListingResponseDTO> variants;
  private ProductDetailListingResponseDTO primaryVariant;
}
