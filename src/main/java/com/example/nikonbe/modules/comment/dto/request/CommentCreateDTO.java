package com.example.nikonbe.modules.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDTO {

  @NotNull(message = "Blog ID không được để trống")
  private Integer blogId;

  private Integer customerId;

  private String userComment;

  @NotBlank(message = "Nội dung bình luận không được để trống")
  private String content;
}


