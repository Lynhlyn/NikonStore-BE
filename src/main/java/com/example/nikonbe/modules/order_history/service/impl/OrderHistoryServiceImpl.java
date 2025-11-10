package com.example.nikonbe.modules.order_history.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.response.PaginationResponse;
import com.example.nikonbe.modules.order_history.dto.request.OrderHistorySearchRequest;
import com.example.nikonbe.modules.order_history.dto.response.OrderHistoryResponse;
import com.example.nikonbe.modules.order_history.entity.OrderHistory;
import com.example.nikonbe.modules.order_history.mapper.OrderHistoryMapper;
import com.example.nikonbe.modules.order_history.repository.OrderHistoryRepository;
import com.example.nikonbe.modules.order_history.service.interF.OrderHistoryService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
  @Autowired private OrderHistoryRepository orderHistoryRepository;

  @Autowired private OrderHistoryMapper orderHistoryMapper;

  @Override
  public ApiResponseDto<List<OrderHistoryResponse>> getAll(Pageable pageable) {
    Page<OrderHistory> orderHistoryPage = orderHistoryRepository.findAll(pageable);

    List<OrderHistoryResponse> responseList =
        orderHistoryPage.getContent().stream()
            .map(orderHistoryMapper::toOrderHistoryResponse)
            .collect(Collectors.toList());

    PaginationResponse pagination =
        PaginationResponse.builder()
            .page(orderHistoryPage.getNumber())
            .size(orderHistoryPage.getSize())
            .totalElements(orderHistoryPage.getTotalElements())
            .totalPages(orderHistoryPage.getTotalPages())
            .build();

    return ApiResponseDto.<List<OrderHistoryResponse>>builder()
        .status(200)
        .message("Success")
        .data(responseList)
        .pagination(pagination)
        .build();
  }

  @Override
  public ApiResponseDto<List<OrderHistoryResponse>> searchOrderHistory(
      OrderHistorySearchRequest searchRequest, Pageable pageable) {
    Status statusAfter = null;
    if (searchRequest.getStatusAfter() != null) {
      try {
        statusAfter = Status.fromValue(searchRequest.getStatusAfter());
      } catch (IllegalArgumentException e) {
        return ApiResponseDto.<List<OrderHistoryResponse>>builder()
            .status(400)
            .message("Trạng thái không hợp lệ")
            .data(List.of())
            .build();
      }
    }

    LocalDate createdAtFrom = null;
    LocalDate createdAtTo = null;
    if (searchRequest.getCreatedAtFrom() != null && !searchRequest.getCreatedAtFrom().isEmpty()) {
      createdAtFrom = LocalDate.parse(searchRequest.getCreatedAtFrom());
    }
    if (searchRequest.getCreatedAtTo() != null && !searchRequest.getCreatedAtTo().isEmpty()) {
      createdAtTo = LocalDate.parse(searchRequest.getCreatedAtTo());
    }

    Page<OrderHistory> orderHistoryPage =
        orderHistoryRepository.searchOrderHistory(
            searchRequest.getTrackingNumber(),
            searchRequest.getOrderType(),
            statusAfter,
            createdAtFrom,
            createdAtTo,
            searchRequest.getChangeByName(),
            searchRequest.getNotes(),
            pageable);

    List<OrderHistoryResponse> responseList =
        orderHistoryPage.getContent().stream()
            .map(orderHistoryMapper::toOrderHistoryResponse)
            .collect(Collectors.toList());

    PaginationResponse pagination =
        PaginationResponse.builder()
            .page(orderHistoryPage.getNumber())
            .size(orderHistoryPage.getSize())
            .totalElements(orderHistoryPage.getTotalElements())
            .totalPages(orderHistoryPage.getTotalPages())
            .build();

    return ApiResponseDto.<List<OrderHistoryResponse>>builder()
        .status(200)
        .message("Tìm kiếm lịch sử đơn hàng thành công")
        .data(responseList)
        .pagination(pagination)
        .build();
  }
}
