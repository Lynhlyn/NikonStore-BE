package com.example.nikonbe.modules.attributes.color.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật màu sắc")
public class ColorUpdateDTO {
  @NotBlank(message = "Tên màu sắc là bắt buộc")
  @Schema(description = "Tên màu sắc", example = "Blue", required = true)
  private String name;

  @Schema(description = "Mã màu hex", example = "#0000FF")
  private String hexCode;

  @NotNull(message = "Trạng thái là bắt buộc")
  @Schema(
      description = "Trạng thái màu sắc",
      example = "ACTIVE",
      required = true,
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;
}
