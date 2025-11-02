package com.example.nikonbe.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Phản hồi đăng nhập admin")
public class AdminLoginResponse {

  @Schema(description = "ID nhân viên", example = "1")
  private Integer id;

  @Schema(description = "Email", example = "admin@example.com")
  private String email;

  @Schema(description = "Họ tên", example = "Nguyễn Văn Admin")
  private String fullName;

  @Schema(description = "Vai trò", example = "ADMIN")
  private String role;

  @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;
}
