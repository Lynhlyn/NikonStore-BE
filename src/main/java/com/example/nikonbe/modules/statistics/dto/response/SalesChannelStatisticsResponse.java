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
public class SalesChannelStatisticsResponse {
  private String channel;
  private Long totalOrders;
  private BigDecimal totalRevenue;
  private BigDecimal averageOrderValue;
  private Long completedOrders;
  private Long cancelledOrders;
  private Double completionRate;
  private Double revenuePercentage;
}

