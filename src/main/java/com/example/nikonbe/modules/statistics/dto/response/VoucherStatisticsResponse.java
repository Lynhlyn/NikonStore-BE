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
public class VoucherStatisticsResponse {
  private Long voucherId;
  private String voucherCode;
  private String description;
  private String discountType;
  private BigDecimal discountValue;
  private Integer totalQuantity;
  private Integer usedCount;
  private Integer remainingQuantity;
  private BigDecimal totalDiscountAmount;
  private Long orderCount;
  private LocalDate startDate;
  private LocalDate endDate;
  private String status;

  public VoucherStatisticsResponse(
      Long voucherId,
      String voucherCode,
      String description,
      String discountType,
      BigDecimal discountValue,
      Integer totalQuantity,
      Integer usedCount,
      Integer remainingQuantity,
      BigDecimal totalDiscountAmount,
      Long orderCount,
      java.sql.Date startDate,
      java.sql.Date endDate,
      String status) {
    this.voucherId = voucherId;
    this.voucherCode = voucherCode;
    this.description = description;
    this.discountType = discountType;
    this.discountValue = discountValue;
    this.totalQuantity = totalQuantity;
    this.usedCount = usedCount;
    this.remainingQuantity = remainingQuantity;
    this.totalDiscountAmount = totalDiscountAmount;
    this.orderCount = orderCount;
    this.startDate = startDate != null ? startDate.toLocalDate() : null;
    this.endDate = endDate != null ? endDate.toLocalDate() : null;
    this.status = status;
  }

  public VoucherStatisticsResponse(
      Long voucherId,
      String voucherCode,
      String description,
      String discountType,
      BigDecimal discountValue,
      Integer totalQuantity,
      Integer usedCount,
      Integer remainingQuantity,
      BigDecimal totalDiscountAmount,
      Long orderCount,
      LocalDate startDate,
      LocalDate endDate,
      String status) {
    this.voucherId = voucherId;
    this.voucherCode = voucherCode;
    this.description = description;
    this.discountType = discountType;
    this.discountValue = discountValue;
    this.totalQuantity = totalQuantity;
    this.usedCount = usedCount;
    this.remainingQuantity = remainingQuantity;
    this.totalDiscountAmount = totalDiscountAmount;
    this.orderCount = orderCount;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
  }
}

