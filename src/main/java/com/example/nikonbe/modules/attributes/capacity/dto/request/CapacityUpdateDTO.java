package com.example.nikonbe.modules.attributes.capacity.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật dung tích")
public class CapacityUpdateDTO {
  @NotBlank(message = "Capacity name is required")
  @Schema(description = "Tên dung tích", example = "20L", required = true)
  private String name;

  @NotNull(message = "Status is required")
  @Schema(
      description = "Trạng thái dung tích",
      example = "ACTIVE",
      required = true,
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;

  @Schema(description = "Số lít", example = "20.00")
  private Double liters;
}
