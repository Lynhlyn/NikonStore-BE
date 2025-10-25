package com.example.nikonbe.modules.attributes.material.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của chất liệu")
public class MaterialResponseDTO {
  @Schema(description = "ID của chất liệu", example = "1")
  private Integer id;

  @Schema(description = "Tên chất liệu", example = "Da")
  private String name;

  @Schema(description = "Mô tả chất liệu", example = "Chất liệu da cao cấp")
  private String description;

  @Schema(description = "Trạng thái chất liệu", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-04-27T14:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-04-27T15:00:00")
  private LocalDateTime updatedAt;
}
