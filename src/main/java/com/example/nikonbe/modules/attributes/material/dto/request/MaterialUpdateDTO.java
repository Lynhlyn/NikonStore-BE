package com.example.nikonbe.modules.attributes.material.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật chất liệu")
public class MaterialUpdateDTO {
  @NotBlank(message = "Tên chất liệu là bắt buộc")
  @Schema(description = "Tên chất liệu", example = "Da tổng hợp", required = true)
  private String name;

  @Schema(description = "Mô tả chất liệu", example = "Chất liệu da tổng hợp")
  private String description;

  @NotNull(message = "Trạng thái là bắt buộc")
  @Schema(
      description = "Trạng thái chất liệu",
      example = "ACTIVE",
      required = true,
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;
}
