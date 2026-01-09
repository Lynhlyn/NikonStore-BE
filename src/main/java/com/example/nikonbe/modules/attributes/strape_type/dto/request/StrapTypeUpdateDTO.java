package com.example.nikonbe.modules.attributes.strape_type.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật loại dây đeo")
public class StrapTypeUpdateDTO {
  @NotBlank(message = "Tên loại dây đeo là bắt buộc")
  @Schema(description = "Tên loại dây đeo", example = "Dây da", required = true)
  private String name;

  @Schema(description = "Mô tả loại dây đeo", example = "Dây da cao cấp")
  private String description;

  @NotNull(message = "Trạng thái là bắt buộc")
  @Schema(
      description = "Trạng thái loại dây đeo",
      example = "ACTIVE",
      required = true,
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;
}
