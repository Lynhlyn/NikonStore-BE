package com.example.nikonbe.security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResult {
  private boolean success;
  private String error;

  public static VerificationResult success() {
    return VerificationResult.builder().success(true).build();
  }

  public static VerificationResult error(String error) {
    return VerificationResult.builder().success(false).error(error).build();
  }
}

