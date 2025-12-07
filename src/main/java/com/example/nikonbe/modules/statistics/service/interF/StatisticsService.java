package com.example.nikonbe.modules.statistics.service.interF;

import com.example.nikonbe.modules.statistics.dto.request.StatisticsFilterRequest;
import com.example.nikonbe.modules.statistics.dto.response.CustomerStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.GeneralStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.OrderStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.ProductStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.RevenueStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.SalesChannelStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.VoucherStatisticsResponse;
import java.util.List;

public interface StatisticsService {

  OrderStatisticsResponse getOrderStatistics(StatisticsFilterRequest filter);

  List<ProductStatisticsResponse> getProductStatistics(StatisticsFilterRequest filter);

  List<ProductStatisticsResponse> getTopSellingProducts(StatisticsFilterRequest filter, int limit);

  List<RevenueStatisticsResponse> getRevenueStatistics(StatisticsFilterRequest filter);

  List<CustomerStatisticsResponse> getCustomerStatistics(StatisticsFilterRequest filter);

  List<SalesChannelStatisticsResponse> getSalesChannelStatistics(StatisticsFilterRequest filter);

  GeneralStatisticsResponse getGeneralStatistics(StatisticsFilterRequest filter);

  List<RevenueStatisticsResponse> getMonthlyRevenueStatistics(Integer year);

  List<RevenueStatisticsResponse> getYearlyRevenueStatistics(int startYear, int endYear);

  List<CustomerStatisticsResponse> getMonthlyCustomerStatistics(Integer year);

  List<CustomerStatisticsResponse> getYearlyCustomerStatistics(int startYear, int endYear);

  List<VoucherStatisticsResponse> getVoucherStatistics(StatisticsFilterRequest filter);

  List<VoucherStatisticsResponse> getTopUsedVouchers(StatisticsFilterRequest filter, int limit);
}

