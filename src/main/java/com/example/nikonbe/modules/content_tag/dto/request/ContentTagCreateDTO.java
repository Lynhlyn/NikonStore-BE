package com.example.nikonbe.modules.content_tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới content tag")
public class ContentTagCreateDTO {
  @NotBlank(message = "Tên tag không được để trống")
  @Schema(description = "Tên tag", example = "Tin tức", required = true)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Schema(description = "Slug", example = "tin-tuc", required = true)
  private String slug;

  @NotBlank(message = "Type không được để trống")
  @Schema(description = "Loại tag", example = "NEWS", required = true)
  private String type;
}
