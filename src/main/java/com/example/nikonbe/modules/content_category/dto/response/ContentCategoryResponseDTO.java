package com.example.nikonbe.modules.content_category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi danh mục nội dung")
public class ContentCategoryResponseDTO {
  @Schema(description = "ID danh mục", example = "1")
  private Integer id;

  @Schema(description = "Tên danh mục", example = "Tin tức")
  private String name;

  @Schema(description = "Slug", example = "tin-tuc")
  private String slug;

  @Schema(description = "Mô tả", example = "Danh mục tin tức")
  private String description;

  @Schema(description = "Loại danh mục", example = "NEWS")
  private String type;

  @Schema(description = "Thời gian tạo", example = "2025-06-16T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-06-16T11:00:00")
  private LocalDateTime updatedAt;
}
