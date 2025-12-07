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
public class ProductStatisticsResponse {
  private Integer productId;
  private String productName;
  private String brandName;
  private String categoryName;
  private Long totalSold;
  private BigDecimal totalRevenue;
  private BigDecimal averagePrice;
  private Long orderCount;
}

