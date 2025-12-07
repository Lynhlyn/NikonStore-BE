package com.example.nikonbe.modules.customer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for updating customer information by client")
public class CustomerClientUpdateDTO {

  @Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
  @Pattern(
      regexp = "^[a-zA-Z0-9_]+$",
      message = "Username can only contain letters, numbers and underscore")
  @Schema(description = "Username", example = "user123")
  private String username;

  @Email(message = "Invalid email format")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Schema(description = "Customer email", example = "user@example.com")
  private String email;

  @Size(min = 2, max = 100, message = "Full name must be between 2-100 characters")
  @Schema(description = "Customer full name", example = "Nguyen Van A")
  private String fullName;

  @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must have 10-11 digits")
  @Schema(description = "Customer phone number", example = "0123456789")
  private String phoneNumber;

  @Schema(description = "Customer date of birth", example = "1990-01-01")
  private LocalDate dateOfBirth;

  @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female or Other")
  @Schema(
      description = "Gender",
      example = "Male",
      allowableValues = {"Male", "Female", "Other"})
  private String gender;

  @Schema(description = "Profile image URL", example = "https://example.com/image.jpg")
  private String urlImage;
}
