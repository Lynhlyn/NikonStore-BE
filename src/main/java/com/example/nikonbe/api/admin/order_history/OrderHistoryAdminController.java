package com.example.nikonbe.api.admin.order_history;

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
@RequestMapping("${api.admin.version}/order-history")
@Tag(
    name = "Admin-Order History",
    description = "API quản lý lịch sử đơn hàng dành cho quản trị viên")
public class OrderHistoryAdminController {

  @Autowired private OrderHistoryService orderHistoryService;

  @GetMapping
  @Operation(
      summary = "Lấy tất cả lịch sử đơn hàng",
      description = "Lấy danh sách tất cả lịch sử đơn hàng với phân trang")
  public ResponseEntity<ApiResponseDto<List<OrderHistoryResponse>>> getAllOrderHistory(
      @PageableDefault(size = 10) Pageable pageable) {
    ApiResponseDto<List<OrderHistoryResponse>> response = orderHistoryService.getAll(pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  @Operation(
      summary = "Tìm kiếm lịch sử đơn hàng",
      description =
          "Tìm kiếm lịch sử đơn hàng theo các tiêu chí: mã đơn hàng, trạng thái mới, thời gian thay đổi, người thay đổi, lý do")
  public ResponseEntity<ApiResponseDto<List<OrderHistoryResponse>>> searchOrderHistory(
      @Parameter(description = "Thông tin tìm kiếm") @ModelAttribute
          OrderHistorySearchRequest searchRequest,
      @PageableDefault(size = 10) Pageable pageable) {
    ApiResponseDto<List<OrderHistoryResponse>> response =
        orderHistoryService.searchOrderHistory(searchRequest, pageable);
    return ResponseEntity.ok(response);
  }
}
