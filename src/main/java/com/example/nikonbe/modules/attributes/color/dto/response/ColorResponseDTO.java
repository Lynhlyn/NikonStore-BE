package com.example.nikonbe.modules.attributes.color.dto.response;

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
@Schema(description = "Thông tin phản hồi của màu sắc")
public class ColorResponseDTO {
  @Schema(description = "ID của màu sắc", example = "1")
  private Integer id;

  @Schema(description = "Tên màu sắc", example = "Red")
  private String name;

  @Schema(description = "Mã màu hex", example = "#FF0000")
  private String hexCode;

  @Schema(description = "Trạng thái màu sắc", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-04-27T14:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-04-27T15:00:00")
  private LocalDateTime updatedAt;
}
