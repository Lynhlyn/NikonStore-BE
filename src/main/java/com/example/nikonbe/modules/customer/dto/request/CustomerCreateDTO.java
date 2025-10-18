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

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Schema(description = "Customer username", example = "john_doe", required = true)
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  @Schema(description = "Customer password", example = "password123", required = true)
  private String password;

  @NotBlank(message = "Email is required")
  @Email(message = "Email format is invalid")
  @Schema(description = "Customer email", example = "john@example.com", required = true)
  private String email;

  @NotBlank(message = "Full name is required")
  @Size(max = 255, message = "Full name must not exceed 255 characters")
  @Schema(description = "Customer full name", example = "John Doe", required = true)
  private String fullName;

  @NotBlank(message = "Phone number is required")
  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  @Schema(description = "Customer phone number", example = "0123456789", required = true)
  private String phoneNumber;

  @Schema(description = "Customer image URL", example = "https://example.com/image.jpg")
  private String urlImage;

  @Schema(description = "Customer date of birth", example = "1990-01-01")
  private String dateOfBirth;

  @Schema(description = "Customer gender", example = "Male", allowableValues = {"Male", "Female", "Other"})
  private String gender;

  @Schema(description = "Whether customer is guest", example = "false")
  private Boolean isGuest;

  @NotNull(message = "Status is required")
  @Schema(description = "Customer status", example = "ACTIVE", required = true)
  private Status status;
}
