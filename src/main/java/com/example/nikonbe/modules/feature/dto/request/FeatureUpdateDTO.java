package com.example.nikonbe.modules.feature.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeatureUpdateDTO {

  @NotBlank(message = "Feature name is required")
  private String name;

  private String description;

  private String featureGroup;
}
