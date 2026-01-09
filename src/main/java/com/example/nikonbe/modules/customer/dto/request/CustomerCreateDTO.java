package com.example.nikonbe.modules.customer.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating a new customer")
public class CustomerCreateDTO {

  @NotBlank(message = "Tên đăng nhập là bắt buộc")
  @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
  @Schema(description = "Customer username", example = "john_doe", required = true)
  private String username;

  @NotBlank(message = "Mật khẩu là bắt buộc")
  @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 100 ký tự")
  @Schema(description = "Customer password", example = "password123", required = true)
  private String password;

  @NotBlank(message = "Email là bắt buộc")
  @Email(message = "Email không hợp lệ")
  @Schema(description = "Customer email", example = "john@example.com", required = true)
  private String email;

  @NotBlank(message = "Họ và tên là bắt buộc")
  @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
  @Schema(description = "Customer full name", example = "John Doe", required = true)
  private String fullName;

  @NotBlank(message = "Số điện thoại là bắt buộc")
  @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
  @Schema(description = "Customer phone number", example = "0123456789", required = true)
  private String phoneNumber;

  @Schema(description = "Customer image URL", example = "https://example.com/image.jpg")
  private String urlImage;

  @Schema(description = "Customer date of birth", example = "1990-01-01")
  private String dateOfBirth;

  @Schema(
      description = "Customer gender",
      example = "Male",
      allowableValues = {"Male", "Female", "Other"})
  private String gender;

  @Schema(description = "Whether customer is guest", example = "false")
  private Boolean isGuest;

  @NotNull(message = "Trạng thái là bắt buộc")
  @Schema(description = "Customer status", example = "ACTIVE", required = true)
  private Status status;
}
