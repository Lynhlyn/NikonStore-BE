package com.example.nikonbe.modules.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateDTO {

  @Min(1)
  @Max(5)
  private Integer rating;

  private String comment;

  private List<String> imageUrls;

  private Integer status;
}
