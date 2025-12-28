package com.example.nikonbe.modules.statistics.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralStatisticsResponse {
  private OrderStatisticsResponse orderStatistics;
  private List<ProductStatisticsResponse> topSellingProducts;
  private List<RevenueStatisticsResponse> revenueByDate;
  private List<CustomerStatisticsResponse> customerGrowth;
  private List<SalesChannelStatisticsResponse> salesChannelComparison;
  private List<VoucherStatisticsResponse> voucherUsage;
  private List<TopCustomerStatisticsResponse> topCustomersByCompletedOrders;
  private List<TopCustomerStatisticsResponse> topCustomersByCancelledOrders;
  private BigDecimal totalRevenue;
  private BigDecimal totalShippingFee;
  private Long totalOrders;
  private Long totalCustomers;
  private Long totalProducts;
  private Long totalVouchers;
  private BigDecimal totalDiscountAmount;
}

