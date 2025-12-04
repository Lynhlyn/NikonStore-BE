package com.example.nikonbe.modules.customer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for changing password")
public class ChangePasswordDTO {

  @NotBlank(message = "Mật khẩu hiện tại không được để trống")
  @Schema(description = "Current password", example = "oldPassword123")
  private String currentPassword;

  @NotBlank(message = "Mật khẩu mới không được để trống")
  @Size(min = 8, max = 32, message = "Mật khẩu phải có từ 8-32 ký tự")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
      message = "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số")
  @Schema(description = "New password", example = "newPassword123")
  private String newPassword;

  @NotBlank(message = "Xác nhận mật khẩu không được để trống")
  @Schema(description = "Confirm new password", example = "newPassword123")
  private String confirmPassword;
}
