package com.example.nikonbe.modules.attributes.category.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của danh mục")
public class CategoryResponseDTO {
  @Schema(description = "ID của danh mục", example = "1")
  private Integer id;

  @Schema(description = "Tên danh mục", example = "Máy ảnh")
  private String name;

  @Schema(description = "ID danh mục cha", example = "2")
  private Integer parentId;

  @Schema(description = "Tên danh mục cha", example = "Thiết bị điện tử")
  private String parentName;

  @Schema(description = "Mô tả danh mục", example = "Danh mục máy ảnh các loại")
  private String description;

  @Schema(description = "Trạng thái danh mục", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-04-27T14:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-04-27T15:00:00")
  private LocalDateTime updatedAt;
}
