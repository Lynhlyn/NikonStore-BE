package com.example.nikonbe.modules.order_history.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistorySearchRequest {
  private String trackingNumber;
  private Integer statusAfter;
  private String createdAtFrom;
  private String createdAtTo;
  private String changeByName;
  private String notes;
  private String orderType;
}
