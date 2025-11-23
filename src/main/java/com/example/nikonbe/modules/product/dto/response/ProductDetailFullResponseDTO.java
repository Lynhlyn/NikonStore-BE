package com.example.nikonbe.modules.product.dto.response;

import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailWithImageResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.review.dto.response.ProductReviewSummaryDTO;
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
public class ProductDetailFullResponseDTO {
  private Integer productId;
  private String name;
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
  private List<ProductDetailWithImageResponseDTO> variants;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private BigDecimal minPriceDiscount;
  private List<PromotionResponseDTO> availablePromotions;
  private ProductReviewSummaryDTO reviewSummary;
}
