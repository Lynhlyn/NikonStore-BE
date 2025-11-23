package com.example.nikonbe.modules.review.dto.response;

import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
  private Integer id;
  private Integer productId;
  private CustomerResponseDTO customer;
  private Integer rating;
  private String comment;
  private Integer status;
  private List<ReviewImageResponseDTO> reviewImages;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
