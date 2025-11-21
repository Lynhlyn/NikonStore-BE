package com.example.nikonbe.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendVerificationEmailRequest {

  @NotBlank(message = "Email là bắt buộc")
  @Email(message = "Email không hợp lệ")
  private String email;
}
