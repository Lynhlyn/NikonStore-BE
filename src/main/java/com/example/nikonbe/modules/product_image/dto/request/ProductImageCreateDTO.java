package com.example.nikonbe.modules.product_image.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageCreateDTO {

  @NotNull(message = "Product ID is required")
  private Integer productId;

  @NotBlank(message = "Image URL is required")
  private String imageUrl;

  @Builder.Default private Boolean isPrimary = false;

  @Builder.Default private Integer sortOrder = 0;

  private String altText;
}
