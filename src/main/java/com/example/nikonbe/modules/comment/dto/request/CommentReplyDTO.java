package com.example.nikonbe.modules.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentReplyDTO {

  @NotNull(message = "Blog ID không được để trống")
  private Integer blogId;

  @NotNull(message = "Parent comment ID không được để trống")
  private Integer parentId;

  private Integer customerId;

  private String userComment;

  @NotBlank(message = "Nội dung bình luận không được để trống")
  private String content;
}


