package com.example.nikonbe.modules.product_tag.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTagResponseDTO {

  private Integer productId;

  private Integer tagId;

  private String tagName;

  private String tagSlug;

  private String tagDescription;

  private Integer tagStatus;
}
