package com.example.nikonbe.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Yêu cầu đăng nhập admin")
public class AdminLoginRequest {

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không đúng định dạng")
  @Schema(description = "Email", example = "admin@example.com", required = true)
  private String email;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Schema(description = "Mật khẩu", example = "password123", required = true)
  private String password;

  @NotBlank(message = "Vai trò không được để trống")
  @Schema(description = "Vai trò", example = "ADMIN", required = true)
  private String role;
}
