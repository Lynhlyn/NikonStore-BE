package com.example.nikonbe.common.utils;

import com.example.nikonbe.common.response.PaginationResponse;
import org.springframework.data.domain.Page;

public class PaginationUtils {

  public static <T> PaginationResponse createPaginationResponse(Page<T> page) {
    return PaginationResponse.builder()
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }
}
