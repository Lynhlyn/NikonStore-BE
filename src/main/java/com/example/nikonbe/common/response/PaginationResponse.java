package com.example.nikonbe.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse {
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
}
