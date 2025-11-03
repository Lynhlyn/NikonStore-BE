package com.example.nikonbe.modules.product_feature.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeatureUpdateDTO {

  @NotNull(message = "Feature IDs are required")
  private List<Integer> featureIds;
}
