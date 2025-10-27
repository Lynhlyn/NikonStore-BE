package com.example.nikonbe.modules.attributes.brand.dto.request;

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
@Schema(description = "Đối tượng yêu cầu tạo mới thương hiệu")
public class BrandCreateDTO {
  @NotBlank(message = "Brand name is required")
  @Schema(description = "Tên thương hiệu", example = "Nikon", required = true)
  private String name;

  @NotNull(message = "Status is required")
  @Schema(description = "Trạng thái thương hiệu", example = "ACTIVE", required = true)
  private Status status;
}
