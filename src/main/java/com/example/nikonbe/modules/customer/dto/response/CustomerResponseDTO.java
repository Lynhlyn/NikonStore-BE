package com.example.nikonbe.modules.customer.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO for customer information")
public class CustomerResponseDTO {

  @Schema(description = "Customer ID", example = "1")
  private Integer id;

  @Schema(description = "Customer username", example = "john_doe")
  private String username;

  @Schema(description = "Customer email", example = "john@example.com")
  private String email;

  @Schema(description = "Customer full name", example = "John Doe")
  private String fullName;

  @Schema(description = "Customer phone number", example = "0123456789")
  private String phoneNumber;

  @Schema(description = "Customer image URL", example = "https://example.com/image.jpg")
  private String urlImage;

  @Schema(description = "Customer date of birth", example = "1990-01-01")
  private LocalDate dateOfBirth;

  @Schema(description = "Customer gender", example = "Male")
  private String gender;

  @Schema(description = "Whether customer is guest", example = "false")
  private Boolean isGuest;

  @Schema(description = "OAuth provider", example = "LOCAL")
  private String provider;

  @Schema(description = "Provider ID", example = "12345")
  private String providerId;

  @Schema(description = "Customer status", example = "ACTIVE")
  private Status status;

  @Schema(description = "Creation timestamp", example = "2024-01-01T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Last update timestamp", example = "2024-01-01T10:00:00")
  private LocalDateTime updatedAt;
}
