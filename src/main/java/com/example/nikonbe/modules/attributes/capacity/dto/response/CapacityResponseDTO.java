package com.example.nikonbe.modules.attributes.capacity.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của dung tích")
public class CapacityResponseDTO {
  @Schema(description = "ID của dung tích", example = "1")
  private Integer id;

  @Schema(description = "Tên dung tích", example = "20L")
  private String name;

  @Schema(description = "Trạng thái dung tích", example = "ACTIVE")
  private Status status;

  @Schema(description = "Số lít", example = "20.00")
  private Double liters;

  @Schema(description = "Thời gian tạo", example = "2025-04-27T14:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-04-27T15:00:00")
  private LocalDateTime updatedAt;
}
