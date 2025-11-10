package com.example.nikonbe.modules.order_history.service.interF;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.modules.order_history.dto.request.OrderHistorySearchRequest;
import com.example.nikonbe.modules.order_history.dto.response.OrderHistoryResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderHistoryService {
  ApiResponseDto<List<OrderHistoryResponse>> getAll(Pageable pageable);

  ApiResponseDto<List<OrderHistoryResponse>> searchOrderHistory(
      OrderHistorySearchRequest searchRequest, Pageable pageable);
}
