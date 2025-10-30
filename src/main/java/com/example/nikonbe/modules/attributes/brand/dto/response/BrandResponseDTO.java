package com.example.nikonbe.modules.attributes.brand.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của thương hiệu")
public class BrandResponseDTO {
  @Schema(description = "ID của thương hiệu", example = "1")
  private Integer id;

  @Schema(description = "Tên thương hiệu", example = "Nikon")
  private String name;

  @Schema(description = "Trạng thái thương hiệu", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-04-27T14:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-04-27T15:00:00")
  private LocalDateTime updatedAt;
}
