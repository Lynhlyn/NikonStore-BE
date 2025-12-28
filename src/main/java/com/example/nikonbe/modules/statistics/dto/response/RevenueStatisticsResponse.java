package com.example.nikonbe.modules.statistics.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsResponse {
  private LocalDate date;
  private BigDecimal dailyRevenue;
  private BigDecimal shippingFee;
  private BigDecimal netRevenue;
  private Long dailyOrders;
  private BigDecimal onlineRevenue;
  private BigDecimal posRevenue;
  private Long onlineOrders;
  private Long posOrders;

  public RevenueStatisticsResponse(
      java.sql.Date date,
      BigDecimal dailyRevenue,
      BigDecimal shippingFee,
      BigDecimal netRevenue,
      Long dailyOrders,
      BigDecimal onlineRevenue,
      BigDecimal posRevenue,
      Long onlineOrders,
      Long posOrders) {
    this.date = date != null ? date.toLocalDate() : null;
    this.dailyRevenue = dailyRevenue;
    this.shippingFee = shippingFee;
    this.netRevenue = netRevenue;
    this.dailyOrders = dailyOrders;
    this.onlineRevenue = onlineRevenue;
    this.posRevenue = posRevenue;
    this.onlineOrders = onlineOrders;
    this.posOrders = posOrders;
  }
}

