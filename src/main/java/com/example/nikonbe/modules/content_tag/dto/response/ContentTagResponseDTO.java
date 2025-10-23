package com.example.nikonbe.modules.content_tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi content tag")
public class ContentTagResponseDTO {
  @Schema(description = "ID tag", example = "1")
  private Integer id;

  @Schema(description = "Tên tag", example = "Tin tức")
  private String name;

  @Schema(description = "Slug", example = "tin-tuc")
  private String slug;

  @Schema(description = "Loại tag", example = "NEWS")
  private String type;

  @Schema(description = "Thời gian tạo", example = "2025-06-16T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-06-16T11:00:00")
  private LocalDateTime updatedAt;
}
