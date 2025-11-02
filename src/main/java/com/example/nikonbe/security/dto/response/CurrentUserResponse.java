package com.example.nikonbe.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin nhân viên đang đăng nhập")
public class CurrentUserResponse {

  @Schema(description = "ID nhân viên", example = "1")
  private Integer id;

  @Schema(description = "Email", example = "admin@example.com")
  private String email;

  @Schema(description = "Họ tên", example = "Nguyễn Văn Admin")
  private String name;

  @Schema(description = "Vai trò", example = "ADMIN")
  private String role;

  @Schema(description = "Số điện thoại", example = "0123456789")
  private String phoneNumber;

  @Schema(description = "Trạng thái", example = "ACTIVE")
  private String status;
}
