package com.example.nikonbe.modules.feature.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeatureResponseDTO {

  private Integer id;

  private String name;

  private String description;

  private String featureGroup;

  private String createdAt;

  private String updatedAt;
}
