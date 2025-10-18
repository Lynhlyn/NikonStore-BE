package com.example.nikonbe.modules.staff.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật staff")
public class StaffUpdateDTO {

  @NotBlank(message = "Họ tên không được để trống")
  @Size(min = 2, max = 100, message = "Họ tên phải có độ dài từ 2-100 ký tự")
  @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ tên chỉ được chứa chữ cái và khoảng trắng")
  @Schema(description = "Họ tên", example = "Nguyễn Văn B", required = true)
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

  @NotNull(message = "Trạng thái không được để trống")
  @Schema(description = "Trạng thái", example = "ACTIVE", required = true)
  private Status status;
}
