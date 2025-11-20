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

  @NotBlank(message = "Current password is required")
  @Schema(description = "Current password", example = "oldPassword123")
  private String currentPassword;

  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 32, message = "Password must be between 8-32 characters")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
      message = "Password must contain at least 1 letter and 1 number")
  @Schema(description = "New password", example = "newPassword123")
  private String newPassword;

  @NotBlank(message = "Confirm password is required")
  @Schema(description = "Confirm new password", example = "newPassword123")
  private String confirmPassword;
}
