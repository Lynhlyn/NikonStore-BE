package com.example.nikonbe.modules.comment.dto.response;

import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
  private Integer id;
  private Integer blogId;
  private CustomerResponseDTO customer;
  private String userComment;
  private String content;
  private Integer parentId;
  private List<CommentResponseDTO> replies;
  private Boolean status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}


