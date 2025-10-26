package com.example.nikonbe.modules.content_category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới danh mục nội dung")
public class ContentCategoryCreateDTO {
  @NotBlank(message = "Tên danh mục không được để trống")
  @Schema(description = "Tên danh mục", example = "Tin tức", required = true)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Schema(description = "Slug", example = "tin-tuc", required = true)
  private String slug;

  @Schema(description = "Mô tả", example = "Danh mục tin tức")
  private String description;

  @NotBlank(message = "Type không được để trống")
  @Schema(description = "Loại danh mục", example = "NEWS", required = true)
  private String type;
}
