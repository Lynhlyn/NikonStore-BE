package com.example.nikonbe.modules.product.dto.response;

import com.example.nikonbe.common.enums.Status;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
  private Integer id;
  private String name;
  private String compartment;
  private Integer strapTypeId;
  private String strapTypeName;
  private Integer brandId;
  private String brandName;
  private Integer categoryId;
  private String categoryName;
  private Integer materialId;
  private String materialName;
  private String description;
  private String dimensions;
  private Double weight;
  private String waterproofRating;
  private Status status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
