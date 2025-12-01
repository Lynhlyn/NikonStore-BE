package com.example.nikonbe.modules.page.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO cập nhật page")
public class PageUpdateDto {

  @NotBlank(message = "Tiêu đề không được để trống")
  @Size(max = 255, message = "Tiêu đề không được quá 255 ký tự")
  @Schema(description = "Tiêu đề trang", example = "Về chúng tôi", required = true)
  private String title;

  @Size(max = 255, message = "Slug không được quá 255 ký tự")
  @Schema(description = "Slug URL-friendly (optional)", example = "about-us")
  private String slug;

  @NotBlank(message = "Nội dung không được để trống")
  @Schema(description = "Nội dung HTML của trang", example = "<h1>Về chúng tôi</h1><p>Nội dung...</p>", required = true)
  private String content;
}
