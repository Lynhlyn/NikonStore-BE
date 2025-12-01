package com.example.nikonbe.modules.page.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO response page cho client")
public class PageDto {

  @Schema(description = "Tiêu đề trang", example = "Về chúng tôi")
  private String title;

  @Schema(description = "Slug URL-friendly", example = "about-us")
  private String slug;

  @Schema(description = "Nội dung HTML của trang", example = "<h1>Về chúng tôi</h1><p>Nội dung...</p>")
  private String content;
}
