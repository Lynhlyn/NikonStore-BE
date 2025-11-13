package com.example.nikonbe.modules.email.template.dto.request;

import com.example.nikonbe.common.enums.EmailAction;
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
@Schema(description = "Đối tượng yêu cầu tạo mới template email")
public class TemplateEmailCreateDTO {

  @NotNull(message = "Action is required")
  @Schema(description = "Action của template email", example = "REGISTER_SUCCESS", required = true)
  private EmailAction action;

  @NotBlank(message = "Subject is required")
  @Schema(
      description = "Tiêu đề email",
      example = "Chào mừng bạn đến với hệ thống",
      required = true)
  private String subject;

  @NotBlank(message = "Content is required")
  @Schema(
      description = "Nội dung HTML của email",
      example = "<h1>Xin chào {{name}}</h1><p>Cảm ơn bạn đã đăng ký tài khoản.</p>",
      required = true)
  private String content;
}
