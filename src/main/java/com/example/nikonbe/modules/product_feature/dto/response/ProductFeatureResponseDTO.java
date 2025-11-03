package com.example.nikonbe.modules.product_feature.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeatureResponseDTO {

  private Integer productId;

  private Integer featureId;

  private String featureName;

  private String featureDescription;

  private String featureGroup;
}
