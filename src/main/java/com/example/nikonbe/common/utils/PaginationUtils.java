package com.example.nikonbe.common.utils;

import com.example.nikonbe.common.response.PaginationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {

  public static <T> PaginationResponse createPaginationResponse(Page<T> page) {
    return PaginationResponse.builder()
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

  public static Pageable createPageable(int page, int size, String sort, String direction) {
    Sort.Direction sortDirection =
        "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    return PageRequest.of(page, size, Sort.by(sortDirection, sort));
  }
}
