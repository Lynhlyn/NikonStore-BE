package com.example.nikonbe.modules.contact.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới liên hệ")
public class ContactCreateDTO {
  @NotBlank(message = "Tên liên hệ không được để trống")
  @Schema(description = "Tên liên hệ", example = "Nguyễn Văn A", required = true)
  private String name;

  @NotBlank(message = "Phone không được để trống")
  @Schema(description = "phone liên hệ", example = "0987654321", required = true)
  private String phone;

  @NotBlank(message = "Nội dung không được để trống")
  @Schema(description = "Nội dung liên hệ", example = "Tôi cần hỗ trợ...", required = true)
  private String content;

  @NotNull(message = "Trạng thái không được để trống")
  @Schema(description = "Trạng thái liên hệ", example = "ACTIVE", required = true)
  private Status status;
}
