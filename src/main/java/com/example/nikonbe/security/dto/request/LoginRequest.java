package com.example.nikonbe.security.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  private String login; // email or username
  private String password;
  private Boolean rememberMe;
}
