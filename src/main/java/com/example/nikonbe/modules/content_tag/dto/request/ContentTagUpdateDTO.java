package com.example.nikonbe.modules.content_tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật content tag")
public class ContentTagUpdateDTO {
  @NotBlank(message = "Tên tag không được để trống")
  @Schema(description = "Tên tag", example = "Tin tức cập nhật", required = true)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Schema(description = "Slug", example = "tin-tuc-cap-nhat", required = true)
  private String slug;

  @NotBlank(message = "Type không được để trống")
  @Schema(description = "Loại tag", example = "NEWS", required = true)
  private String type;
}
