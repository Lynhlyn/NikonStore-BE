package com.example.nikonbe.modules.review.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageResponseDTO {
  private Integer id;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
