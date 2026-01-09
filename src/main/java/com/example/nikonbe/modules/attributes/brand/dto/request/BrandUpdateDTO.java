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
@Schema(description = "Đối tượng yêu cầu cập nhật thương hiệu")
public class BrandUpdateDTO {

  @NotBlank(message = "Tên thương hiệu là bắt buộc")
  @Schema(description = "Tên thương hiệu", example = "Nikon Updated", required = true)
  private String name;

  @NotNull(message = "Trạng thái là bắt buộc")
  @Schema(
      description = "Trạng thái thương hiệu",
      example = "ACTIVE",
      required = true,
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;
}
