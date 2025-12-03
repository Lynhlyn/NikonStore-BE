package com.example.nikonbe.modules.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogCreateDTO {

  @NotBlank(message = "Tiêu đề không được để trống")
  private String title;

  @NotBlank(message = "Slug không được để trống")
  private String slug;

  private String summary;

  @NotBlank(message = "Nội dung không được để trống")
  private String content;

  private String thumbnailUrl;

  private Integer staffId;

  private Integer categoryId;

  private Integer tagId;

  @Builder.Default
  private Boolean isPublished = false;
}


