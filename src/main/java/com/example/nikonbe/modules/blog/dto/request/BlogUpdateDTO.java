package com.example.nikonbe.modules.blog.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogUpdateDTO {

  private String title;

  private String slug;

  private String summary;

  private String content;

  private String thumbnailUrl;

  private Integer staffId;

  private Integer categoryId;

  private Integer tagId;

  private Boolean isPublished;
}


