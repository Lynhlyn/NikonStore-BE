package com.example.nikonbe.modules.tag.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDTO {

  @Schema(description = "ID của tag", example = "1")
  private Integer id;

  @Schema(description = "Tên của tag", example = "Technology")
  private String name;

  @Schema(description = "Slug của tag", example = "technology")
  private String slug;

  @Schema(description = "Mô tả của tag", example = "All about technology")
  private String description;

  @Schema(description = "Trạng thái tag", example = "ACTIVE")
  private Status status;

  private String createdAt;

  private String updatedAt;
}
