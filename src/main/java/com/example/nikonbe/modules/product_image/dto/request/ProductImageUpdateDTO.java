package com.example.nikonbe.modules.product_image.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageUpdateDTO {

  private String imageUrl;

  private Boolean isPrimary;

  private Integer sortOrder;

  private String altText;
}
