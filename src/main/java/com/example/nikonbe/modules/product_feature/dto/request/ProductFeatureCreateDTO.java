package com.example.nikonbe.modules.product_feature.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeatureCreateDTO {

  @NotNull(message = "Feature ID is required")
  private Integer featureId;
}
