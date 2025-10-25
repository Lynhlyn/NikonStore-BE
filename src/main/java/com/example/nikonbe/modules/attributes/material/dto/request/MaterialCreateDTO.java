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
@Schema(description = "Đối tượng yêu cầu tạo mới chất liệu")
public class MaterialCreateDTO {
  @NotBlank(message = "Material name is required")
  @Schema(description = "Tên chất liệu", example = "Da", required = true)
  private String name;

  @Schema(description = "Mô tả chất liệu", example = "Chất liệu da cao cấp")
  private String description;

  @NotNull(message = "Status is required")
  @Schema(description = "Trạng thái chất liệu", example = "ACTIVE", required = true)
  private Status status;
}
