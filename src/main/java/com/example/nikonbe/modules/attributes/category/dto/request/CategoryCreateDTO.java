package com.example.nikonbe.modules.attributes.category.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới danh mục")
public class CategoryCreateDTO {
  @NotBlank(message = "Category name is required")
  @Schema(description = "Tên danh mục", example = "Máy ảnh", required = true)
  private String name;

  @Schema(description = "ID danh mục cha", example = "2")
  private Integer parentId;

  @Schema(description = "Mô tả danh mục", example = "Danh mục máy ảnh các loại")
  private String description;

  @NotNull(message = "Status is required")
  @Schema(description = "Trạng thái danh mục", example = "ACTIVE", required = true)
  private Status status;
}
