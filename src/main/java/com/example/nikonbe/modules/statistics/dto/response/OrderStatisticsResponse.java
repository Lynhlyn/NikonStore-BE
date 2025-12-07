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
public class OrderStatisticsResponse {
  private Long totalOrders;
  private Long completedOrders;
  private Long cancelledOrders;
  private Long pendingOrders;
  private BigDecimal totalRevenue;
  private BigDecimal averageOrderValue;
  private Long onlineOrders;
  private Long posOrders;
  private BigDecimal onlineRevenue;
  private BigDecimal posRevenue;
}

