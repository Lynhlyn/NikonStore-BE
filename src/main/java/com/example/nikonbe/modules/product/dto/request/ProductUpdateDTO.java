package com.example.nikonbe.modules.product.dto.request;

import com.example.nikonbe.common.enums.Status;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDTO {

  private String name;

  private String compartment;

  private Integer strapTypeId;

  private Integer brandId;

  private Integer categoryId;

  private Integer materialId;

  private String description;

  private String dimensions;

  private Double weight;

  private String waterproofRating;

  private Status status;
}
