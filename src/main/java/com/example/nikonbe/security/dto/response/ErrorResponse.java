package com.example.nikonbe.security.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  private String error;
  private Map<String, Object> details;

  public ErrorResponse(String error) {
    this.error = error;
  }
}
