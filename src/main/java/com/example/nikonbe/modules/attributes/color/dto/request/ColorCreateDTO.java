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
@Schema(description = "Đối tượng yêu cầu tạo mới màu sắc")
public class ColorCreateDTO {
  @NotBlank(message = "Tên màu sắc là bắt buộc")
  @Schema(description = "Tên màu sắc", example = "Red", required = true)
  private String name;

  @Schema(description = "Mã màu hex", example = "#FF0000")
  private String hexCode;

  @NotNull(message = "Trạng thái là bắt buộc")
  @Schema(description = "Trạng thái màu sắc", example = "ACTIVE", required = true)
  private Status status;
}
