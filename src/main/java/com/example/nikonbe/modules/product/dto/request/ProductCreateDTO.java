package com.example.nikonbe.modules.product.dto.request;

import com.example.nikonbe.common.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDTO {

  @NotBlank private String name;

  private String compartment;

  private Integer strapTypeId;

  private Integer brandId;

  private Integer categoryId;

  private Integer materialId;

  private String description;

  private String dimensions;

  private Double weight;

  private String waterproofRating;

  @NotNull private Status status;
}
