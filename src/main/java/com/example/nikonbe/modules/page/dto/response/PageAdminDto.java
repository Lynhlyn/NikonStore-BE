package com.example.nikonbe.modules.page.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO response page cho admin")
public class PageAdminDto {

  @Schema(description = "ID của page", example = "1")
  private Long id;

  @Schema(description = "Tiêu đề trang", example = "Về chúng tôi")
  private String title;

  @Schema(description = "Slug URL-friendly", example = "about-us")
  private String slug;

  @Schema(description = "Nội dung HTML của trang", example = "<h1>Về chúng tôi</h1><p>Nội dung...</p>")
  private String content;

  @Schema(description = "Thời gian tạo", example = "2024-01-01T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2024-01-02T15:30:00")
  private LocalDateTime updatedAt;
}
