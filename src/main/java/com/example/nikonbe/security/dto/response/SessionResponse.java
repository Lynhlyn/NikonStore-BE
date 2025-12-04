package com.example.nikonbe.security.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

  private Integer tokenId;
  private LocalDateTime createdAt;
  private Boolean isCurrent;
  private String deviceName;
  private String browserName;
  private String deviceType;
  private String ipAddress;
}

