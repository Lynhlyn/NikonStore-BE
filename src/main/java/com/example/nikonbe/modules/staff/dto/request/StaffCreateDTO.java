package com.example.nikonbe.modules.staff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới staff")
public class StaffCreateDTO {

  @NotBlank(message = "Username không được để trống")
  @Size(min = 3, max = 50, message = "Username phải có độ dài từ 3-50 ký tự")
  @Pattern(
      regexp = "^[a-zA-Z0-9._-]+$",
      message = "Username chỉ được chứa chữ cái, số, dấu chấm, gạch dưới và gạch ngang")
  @Schema(description = "Username", example = "admin123", required = true)
  private String username;

  @NotBlank(message = "Password không được để trống")
  @Size(min = 6, max = 100, message = "Password phải có độ dài từ 6-100 ký tự")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
      message = "Password phải chứa ít nhất 1 chữ thường, 1 chữ hoa, 1 số và 1 ký tự đặc biệt")
  @Schema(description = "Password", example = "Password123!", required = true)
  private String password;

  @NotBlank(message = "Họ tên không được để trống")
  @Size(min = 2, max = 100, message = "Họ tên phải có độ dài từ 2-100 ký tự")
  @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ tên chỉ được chứa chữ cái và khoảng trắng")
  @Schema(description = "Họ tên", example = "Nguyễn Văn A", required = true)
  private String fullName;

  @NotBlank(message = "Số điện thoại không được để trống")
  @Pattern(
      regexp = "^(\\+84|84|0)[1-9][0-9]{8}$",
      message = "Số điện thoại không đúng định dạng Việt Nam")
  @Schema(description = "Số điện thoại", example = "0987654321", required = true)
  private String phoneNumber;

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không đúng định dạng")
  @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@.+$", message = "Email phải chứa ký tự @ và phần đuôi")
  @Schema(description = "Email", example = "staff@anydomain.com", required = true)
  private String email;

  @NotBlank(message = "Vai trò không được để trống")
  @Size(min = 2, max = 50, message = "Vai trò phải có độ dài từ 2-50 ký tự")
  @Schema(description = "Vai trò", example = "ADMIN", required = true)
  private String role;
}
