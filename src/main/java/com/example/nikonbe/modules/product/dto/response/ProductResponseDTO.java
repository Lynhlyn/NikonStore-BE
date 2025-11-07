package com.example.nikonbe.modules.product.dto.response;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.product_feature.dto.response.ProductFeatureResponseDTO;
import com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO;
import com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
  private Integer id;
  private String name;
  private String compartment;
  private StrapTypeResponseDTO strapType;
  private BrandResponseDTO brand;
  private CategoryResponseDTO category;
  private MaterialResponseDTO material;
  private String description;
  private String dimensions;
  private Double weight;
  private String waterproofRating;
  private Status status;
  private List<ProductImageResponseDTO> images;
  private List<ProductTagResponseDTO> tags;
  private List<ProductFeatureResponseDTO> features;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
