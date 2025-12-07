package com.example.nikonbe.modules.statistics.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class CustomerStatisticsResponse {
  private LocalDate date;
  private Long newCustomers;
  private Long totalCustomers;
  private Long activeCustomers;
  private Long guestCustomers;

  public CustomerStatisticsResponse(
      java.sql.Date date,
      Long newCustomers,
      Long totalCustomers,
      Long activeCustomers,
      Long guestCustomers) {
    this.date = date != null ? date.toLocalDate() : null;
    this.newCustomers = newCustomers;
    this.totalCustomers = totalCustomers;
    this.activeCustomers = activeCustomers;
    this.guestCustomers = guestCustomers;
  }

  public CustomerStatisticsResponse(
      LocalDate date,
      Long newCustomers,
      Long totalCustomers,
      Long activeCustomers,
      Long guestCustomers) {
    this.date = date;
    this.newCustomers = newCustomers;
    this.totalCustomers = totalCustomers;
    this.activeCustomers = activeCustomers;
    this.guestCustomers = guestCustomers;
  }
}

