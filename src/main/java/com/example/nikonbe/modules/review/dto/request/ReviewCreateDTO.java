package com.example.nikonbe.modules.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDTO {

  @NotNull private Integer productId;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer rating;

  private String comment;

  @NotNull(message = "orderDetailId là bắt buộc")
  private Integer orderDetailId;

  private List<String> imageUrls;
}
