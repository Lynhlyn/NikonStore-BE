package com.example.nikonbe.modules.statistics.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsFilterRequest {
  private LocalDate fromDate;
  private LocalDate toDate;
  private Integer year;
  private Integer month;
  private String orderType;
  private Integer status;
  private String paymentMethod;
  private Integer categoryId;
  private Integer brandId;
  private Integer productId;
}

