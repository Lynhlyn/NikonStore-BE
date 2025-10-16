package com.example.nikonbe.common.utils;

import org.springframework.data.domain.Page;

import com.example.nikonbe.common.response.PaginationResponse;

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
