package com.example.nikonbe.modules.color_image.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorImageCreateDTO {

  @NotNull(message = "Product ID is required")
  private Integer productId;

  @NotNull(message = "Color ID is required")
  private Integer colorId;

  @NotBlank(message = "Image URL is required")
  private String imageUrl;

  private String altText;
}
