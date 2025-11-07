package com.example.nikonbe.modules.product_image.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponseDTO {

  private Integer id;

  private Integer productId;

  private String imageUrl;

  private Boolean isPrimary;

  private Integer sortOrder;

  private String altText;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
