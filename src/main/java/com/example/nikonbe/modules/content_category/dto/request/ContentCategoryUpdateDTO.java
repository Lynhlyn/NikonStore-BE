package com.example.nikonbe.modules.content_category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật danh mục nội dung")
public class ContentCategoryUpdateDTO {
  @NotBlank(message = "Tên danh mục không được để trống")
  @Schema(description = "Tên danh mục", example = "Tin tức cập nhật", required = true)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Schema(description = "Slug", example = "tin-tuc-cap-nhat", required = true)
  private String slug;

  @Schema(description = "Mô tả", example = "Danh mục tin tức cập nhật")
  private String description;

  @NotBlank(message = "Type không được để trống")
  @Schema(description = "Loại danh mục", example = "NEWS", required = true)
  private String type;
}
