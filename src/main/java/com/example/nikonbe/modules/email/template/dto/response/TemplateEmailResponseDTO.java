package com.example.nikonbe.modules.email.template.dto.response;

import com.example.nikonbe.common.enums.EmailAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng phản hồi template email")
public class TemplateEmailResponseDTO {

  @Schema(description = "ID của template", example = "1")
  private Integer id;

  @Schema(description = "Action của template email", example = "REGISTER_SUCCESS")
  private EmailAction action;

  @Schema(description = "Tiêu đề email", example = "Chào mừng bạn đến với hệ thống")
  private String subject;

  @Schema(description = "Nội dung HTML của email")
  private String content;

  @Schema(description = "Thời gian tạo")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật cuối")
  private LocalDateTime updatedAt;
}
