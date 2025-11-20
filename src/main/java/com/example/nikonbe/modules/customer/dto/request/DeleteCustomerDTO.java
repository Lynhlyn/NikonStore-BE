package com.example.nikonbe.modules.customer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for deleting customer account")
public class DeleteCustomerDTO {

  @NotBlank(message = "Reason is required")
  @Size(min = 10, max = 500, message = "Reason must be between 10-500 characters")
  @Schema(description = "Reason for deleting account", example = "No longer using the service")
  private String reason;
}
