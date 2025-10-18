package com.example.nikonbe.modules.customer.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for filtering customers")
public class CustomerFilterDTO {

  @Schema(description = "Search keyword for username, email, phone, or full name", example = "john")
  private String keyword;

  @Schema(description = "Filter by status", example = "ACTIVE")
  private Status status;

  @Email(message = "Email format is invalid")
  @Schema(description = "Filter by email (partial match)", example = "john@")
  private String email;

  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  @Schema(description = "Filter by phone number (partial match)", example = "0123")
  private String phoneNumber;

  @Size(max = 255, message = "Full name must not exceed 255 characters")
  @Schema(description = "Filter by full name (partial match)", example = "John")
  private String fullName;

  @Schema(description = "Filter by gender", example = "Male", allowableValues = {"Male", "Female", "Other"})
  private String gender;

  @Schema(description = "Filter by OAuth provider", example = "LOCAL")
  private String provider;

  @Schema(description = "Filter by guest status", example = "false")
  private Boolean isGuest;

  @Schema(description = "Filter by creation date from", example = "2024-01-01")
  private LocalDate createdFromDate;

  @Schema(description = "Filter by creation date to", example = "2024-12-31")
  private LocalDate createdToDate;
}
