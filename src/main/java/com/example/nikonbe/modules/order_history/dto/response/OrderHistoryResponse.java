package com.example.nikonbe.modules.order_history.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {
  private Integer orderId;
  private String customerName;
  private String trackingNumber;
  private String orderType;
  private String changeByType;
  private String changeByName;
  private Integer statusBefore;
  private Integer statusAfter;
  private String notes;
  private LocalDateTime createdAt;
}
