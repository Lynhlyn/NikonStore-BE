package com.example.nikonbe.common.utils;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.response.PaginationResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

  public static <T> ResponseEntity<ApiResponseDto<T>> success(
      T data, String message, HttpStatus status) {
    return ResponseEntity.status(status)
        .body(
            ApiResponseDto.<T>builder().status(status.value()).message(message).data(data).build());
  }

  public static <T> ResponseEntity<ApiResponseDto<T>> success(T data, String message) {
    return success(data, message, HttpStatus.OK);
  }

  public static <T> ResponseEntity<ApiResponseDto<List<T>>> successWithPagination(
      List<T> data, PaginationResponse pagination, String message, HttpStatus status) {
    return ResponseEntity.status(status)
        .body(
            ApiResponseDto.<List<T>>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .pagination(pagination)
                .build());
  }

  public static <T> ResponseEntity<ApiResponseDto<List<T>>> successWithPagination(
      List<T> data, PaginationResponse pagination, String message) {
    return successWithPagination(data, pagination, message, HttpStatus.OK);
  }

  public static <T> ResponseEntity<ApiResponseDto<List<T>>> successWithPage(
      Page<T> page, String message, HttpStatus status) {
    PaginationResponse pagination = PaginationUtils.createPaginationResponse(page);
    return successWithPagination(page.getContent(), pagination, message, status);
  }

  public static <T> ResponseEntity<ApiResponseDto<List<T>>> successWithPage(
      Page<T> page, String message) {
    return successWithPage(page, message, HttpStatus.OK);
  }

  public static <T> ResponseEntity<ApiResponseDto<T>> error(String message, HttpStatus status) {
    return ResponseEntity.status(status)
        .body(ApiResponseDto.<T>builder().status(status.value()).message(message).build());
  }
}
