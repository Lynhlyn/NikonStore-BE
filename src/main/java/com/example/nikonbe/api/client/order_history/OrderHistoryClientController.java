package com.example.nikonbe.api.client.order_history;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.modules.order_history.dto.request.OrderHistorySearchRequest;
import com.example.nikonbe.modules.order_history.dto.response.OrderHistoryResponse;
import com.example.nikonbe.modules.order_history.service.interF.OrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.client.version}/order-history")
@Tag(
    name = "Client-Order History",
    description = "API lịch sử đơn hàng dành cho khách hàng")
public class OrderHistoryClientController {

  @Autowired private OrderHistoryService orderHistoryService;

  @GetMapping("/search")
  @Operation(
      summary = "Tìm kiếm lịch sử đơn hàng",
      description =
          "Tìm kiếm lịch sử đơn hàng theo mã đơn hàng (trackingNumber). Khách hàng chỉ có thể xem lịch sử đơn hàng của chính họ.")
  public ResponseEntity<ApiResponseDto<List<OrderHistoryResponse>>> searchOrderHistory(
      @Parameter(description = "Thông tin tìm kiếm") @ModelAttribute
          OrderHistorySearchRequest searchRequest,
      @PageableDefault(size = 100) Pageable pageable) {
    ApiResponseDto<List<OrderHistoryResponse>> response =
        orderHistoryService.searchOrderHistory(searchRequest, pageable);
    return ResponseEntity.ok(response);
  }
}

