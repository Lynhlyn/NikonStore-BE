package com.example.nikonbe.modules.statistics.service.impl;

import com.example.nikonbe.modules.statistics.dto.request.StatisticsFilterRequest;
import com.example.nikonbe.modules.statistics.dto.response.CustomerStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.GeneralStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.OrderStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.ProductStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.RevenueStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.SalesChannelStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.TopCustomerStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.VoucherStatisticsResponse;
import com.example.nikonbe.modules.statistics.repository.StatisticsRepository;
import com.example.nikonbe.modules.statistics.repository.StatisticsRepositoryImpl;
import com.example.nikonbe.modules.statistics.service.interF.StatisticsService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

  private final StatisticsRepository statisticsRepository;
  private final StatisticsRepositoryImpl statisticsRepositoryImpl;

  public StatisticsServiceImpl(
      StatisticsRepository statisticsRepository,
      StatisticsRepositoryImpl statisticsRepositoryImpl) {
    this.statisticsRepository = statisticsRepository;
    this.statisticsRepositoryImpl = statisticsRepositoryImpl;
  }

  @Override
  public OrderStatisticsResponse getOrderStatistics(StatisticsFilterRequest filter) {
    return statisticsRepository.getOrderStatistics(
        filter.getFromDate(),
        filter.getToDate(),
        filter.getYear(),
        filter.getMonth(),
        filter.getOrderType(),
        filter.getStatus() != null
            ? com.example.nikonbe.common.enums.Status.fromValue(filter.getStatus())
            : null);
  }

  @Override
  public List<ProductStatisticsResponse> getProductStatistics(StatisticsFilterRequest filter) {
    return statisticsRepository.getProductStatistics(
        filter.getFromDate(),
        filter.getToDate(),
        filter.getYear(),
        filter.getMonth(),
        filter.getCategoryId(),
        filter.getBrandId(),
        filter.getProductId());
  }

  @Override
  public List<ProductStatisticsResponse> getTopSellingProducts(
      StatisticsFilterRequest filter, int limit) {
    List<ProductStatisticsResponse> products = getProductStatistics(filter);
    return products.stream().limit(limit).collect(Collectors.toList());
  }

  @Override
  public List<RevenueStatisticsResponse> getRevenueStatistics(StatisticsFilterRequest filter) {
    return statisticsRepository.getRevenueStatistics(
        filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());
  }

  @Override
  public List<CustomerStatisticsResponse> getCustomerStatistics(StatisticsFilterRequest filter) {
    return statisticsRepository.getCustomerStatistics(
        filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());
  }

  @Override
  public List<SalesChannelStatisticsResponse> getSalesChannelStatistics(
      StatisticsFilterRequest filter) {
    return statisticsRepository.getSalesChannelStatistics(
        filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());
  }

  @Override
  public GeneralStatisticsResponse getGeneralStatistics(StatisticsFilterRequest filter) {
    OrderStatisticsResponse orderStats = getOrderStatistics(filter);
    List<ProductStatisticsResponse> topProducts = getTopSellingProducts(filter, 10);
    List<RevenueStatisticsResponse> revenueStats = getRevenueStatistics(filter);
    List<CustomerStatisticsResponse> customerStats = getCustomerStatistics(filter);
    List<SalesChannelStatisticsResponse> channelStats = getSalesChannelStatistics(filter);

    java.math.BigDecimal totalRevenue =
        statisticsRepository.getTotalRevenue(
            filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());

    Long totalOrders =
        statisticsRepository.getTotalOrders(
            filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());

    Long totalCustomers =
        statisticsRepository.getTotalCustomers(
            filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());

    Long totalProducts = statisticsRepository.getTotalActiveProducts();

    List<VoucherStatisticsResponse> voucherStats = getVoucherStatistics(filter);
    Long totalVouchers = statisticsRepository.getTotalActiveVouchers();
    java.math.BigDecimal totalDiscountAmount =
        statisticsRepository.getTotalDiscountAmount(
            filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());

    java.math.BigDecimal totalShippingFee =
        statisticsRepository.getTotalShippingFee(
            filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());

    List<TopCustomerStatisticsResponse> topCustomersByCompleted =
        getTopCustomersByCompletedOrders(filter, 10);
    List<TopCustomerStatisticsResponse> topCustomersByCancelled =
        getTopCustomersByCancelledOrders(filter, 10);

    return GeneralStatisticsResponse.builder()
        .orderStatistics(orderStats)
        .topSellingProducts(topProducts)
        .revenueByDate(revenueStats)
        .customerGrowth(customerStats)
        .salesChannelComparison(channelStats)
        .voucherUsage(voucherStats)
        .topCustomersByCompletedOrders(topCustomersByCompleted)
        .topCustomersByCancelledOrders(topCustomersByCancelled)
        .totalRevenue(totalRevenue)
        .totalShippingFee(totalShippingFee)
        .totalOrders(totalOrders)
        .totalCustomers(totalCustomers)
        .totalProducts(totalProducts)
        .totalVouchers(totalVouchers)
        .totalDiscountAmount(totalDiscountAmount)
        .build();
  }

  @Override
  public List<RevenueStatisticsResponse> getMonthlyRevenueStatistics(Integer year) {
    StatisticsFilterRequest filter = StatisticsFilterRequest.builder().year(year).build();
    return getRevenueStatistics(filter);
  }

  @Override
  public List<RevenueStatisticsResponse> getYearlyRevenueStatistics(int startYear, int endYear) {
    StatisticsFilterRequest filter =
        StatisticsFilterRequest.builder()
            .fromDate(LocalDate.of(startYear, 1, 1))
            .toDate(LocalDate.of(endYear, 12, 31))
            .build();
    return getRevenueStatistics(filter);
  }

  @Override
  public List<CustomerStatisticsResponse> getMonthlyCustomerStatistics(Integer year) {
    StatisticsFilterRequest filter = StatisticsFilterRequest.builder().year(year).build();
    return getCustomerStatistics(filter);
  }

  @Override
  public List<CustomerStatisticsResponse> getYearlyCustomerStatistics(int startYear, int endYear) {
    StatisticsFilterRequest filter =
        StatisticsFilterRequest.builder()
            .fromDate(LocalDate.of(startYear, 1, 1))
            .toDate(LocalDate.of(endYear, 12, 31))
            .build();
    return getCustomerStatistics(filter);
  }

  @Override
  public List<VoucherStatisticsResponse> getVoucherStatistics(StatisticsFilterRequest filter) {
    return statisticsRepository.getVoucherStatistics(
        filter.getFromDate(), filter.getToDate(), filter.getYear(), filter.getMonth());
  }

  @Override
  public List<VoucherStatisticsResponse> getTopUsedVouchers(
      StatisticsFilterRequest filter, int limit) {
    return getVoucherStatistics(filter).stream().limit(limit).collect(Collectors.toList());
  }

  @Override
  public List<TopCustomerStatisticsResponse> getTopCustomersByCompletedOrders(
      StatisticsFilterRequest filter, int limit) {
    return statisticsRepositoryImpl.getTopCustomersByCompletedOrders(
        filter.getFromDate(),
        filter.getToDate(),
        filter.getYear(),
        filter.getMonth(),
        limit);
  }

  @Override
  public List<TopCustomerStatisticsResponse> getTopCustomersByCancelledOrders(
      StatisticsFilterRequest filter, int limit) {
    return statisticsRepositoryImpl.getTopCustomersByCancelledOrders(
        filter.getFromDate(),
        filter.getToDate(),
        filter.getYear(),
        filter.getMonth(),
        limit);
  }
}

