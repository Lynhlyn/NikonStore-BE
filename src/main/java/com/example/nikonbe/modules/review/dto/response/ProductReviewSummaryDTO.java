package com.example.nikonbe.modules.review.dto.response;

import java.util.Map;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReviewSummaryDTO {
  private Double averageRating;
  private Long totalReviews;
  private Map<Integer, Long> ratingDistribution;
}
