package com.example.nikonbe.modules.color_image.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorImageResponseDTO {

  private Integer id;
  private Integer productId;
  private Integer colorId;
  private String imageUrl;
  private String altText;
}
