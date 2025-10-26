package com.example.nikonbe.modules.attributes.strape_type.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của loại dây đeo")
public class StrapTypeResponseDTO {
  @Schema(description = "ID của loại dây đeo", example = "1")
  private Integer id;

  @Schema(description = "Tên loại dây đeo", example = "Dây vải")
  private String name;

  @Schema(description = "Mô tả loại dây đeo", example = "Dây vải bền chắc")
  private String description;

  @Schema(description = "Trạng thái loại dây đeo", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-04-27T14:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-04-27T15:00:00")
  private LocalDateTime updatedAt;
}
