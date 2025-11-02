package com.example.nikonbe.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

  @NotBlank(message = "Token là bắt buộc")
  private String token;

  @NotBlank(message = "Mật khẩu mới là bắt buộc")
  @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
  private String newPassword;
}
