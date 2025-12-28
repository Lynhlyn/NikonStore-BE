package com.example.nikonbe.modules.statistics.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerStatisticsResponse {
  private Integer customerId;
  private String customerName;
  private String email;
  private String phoneNumber;
  private Long completedOrdersCount;
  private Long cancelledOrdersCount;
  private BigDecimal totalSpent;
}
