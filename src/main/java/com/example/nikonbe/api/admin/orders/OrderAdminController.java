package com.example.nikonbe.api.admin.orders;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.response.PaginationResponse;
import com.example.nikonbe.modules.orders.dto.request.CancelOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.UpdateStatusOrderRequest;
import com.example.nikonbe.modules.orders.dto.response.GetOrderDetailResponse;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderAllResponse;
import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.orders.service.impl.OrderExcelExportService;
import com.example.nikonbe.modules.orders.service.interF.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/orders")
@Tag(name = "Admin-Orders API", description = "API quản lý đơn hàng dành cho quản trị viên")
@RequiredArgsConstructor
public class OrderAdminController {
  @Autowired private OrderService orderService;
  @Autowired private OrderExcelExportService orderExcelExportService;

  @GetMapping("/all")
  public ResponseEntity<ApiResponseDto<Iterable<OrderAllResponse>>> getAllOrders(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Page<OrderAllResponse> orderPage = orderService.getAllOrders(page, size);
    PaginationResponse pagination =
        PaginationResponse.builder()
            .page(orderPage.getNumber())
            .size(orderPage.getSize())
            .totalElements(orderPage.getTotalElements())
            .totalPages(orderPage.getTotalPages())
            .build();
    ApiResponseDto<Iterable<OrderAllResponse>> response =
        ApiResponseDto.<Iterable<OrderAllResponse>>builder()
            .status(HttpStatus.OK.value())
            .message("All orders retrieved successfully")
            .data(orderPage.getContent())
            .pagination(pagination)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponseDto<Iterable<OrderAllResponse>>> searchOrders(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<OrderAllResponse> orderPage =
        orderService.searchOrders(keyword, type, status, fromDate, toDate, page, size);
    PaginationResponse pagination =
        PaginationResponse.builder()
            .page(orderPage.getNumber())
            .size(orderPage.getSize())
            .totalElements(orderPage.getTotalElements())
            .totalPages(orderPage.getTotalPages())
            .build();
    ApiResponseDto<Iterable<OrderAllResponse>> response =
        ApiResponseDto.<Iterable<OrderAllResponse>>builder()
            .status(HttpStatus.OK.value())
            .message("Orders search result")
            .data(orderPage.getContent())
            .pagination(pagination)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponseDto<GetOrderDetailResponse>> getOrderDetailById(
      @PathVariable Integer orderId) {
    GetOrderDetailResponse orderDetail = orderService.getOrderDetailById(orderId);
    ApiResponseDto<GetOrderDetailResponse> response =
        ApiResponseDto.<GetOrderDetailResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Order details retrieved successfully")
            .data(orderDetail)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/cancel")
  public ResponseEntity<ApiResponseDto<ListOrderResponse>> cancelOrder(
      @RequestBody CancelOrderRequest request) {
    ListOrderResponse order = orderService.cancelOrder(request);

    ApiResponseDto<ListOrderResponse> response =
        ApiResponseDto.<ListOrderResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Order cancelled successfully")
            .data(order)
            .build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/status")
  public ResponseEntity<ApiResponseDto<Order>> updateOrderStatus(
      @RequestBody UpdateStatusOrderRequest request) {
    try {
      orderService.updateOrderStatus(request);
      return ResponseEntity.ok(
          ApiResponseDto.<Order>builder()
              .status(200)
              .message("Order status updated successfully")
              .build());
    } catch (IllegalStateException | IllegalArgumentException ex) {
      return ResponseEntity.badRequest()
          .body(ApiResponseDto.<Order>builder().status(400).message(ex.getMessage()).build());
    }
  }

  @GetMapping("/export/excel")
  public ResponseEntity<byte[]> exportOrdersToExcel(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate) {
    try {
      byte[] excelData =
          orderExcelExportService.exportOrdersToExcel(keyword, type, status, fromDate, toDate);

      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      String filename = "DanhSachDonHang_" + timestamp + ".xlsx";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", filename);
      headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

      return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{orderId}/export/excel")
  public ResponseEntity<byte[]> exportOrderDetailToExcel(@PathVariable Integer orderId) {
    try {
      byte[] excelData = orderExcelExportService.exportOrderDetailsToExcel(orderId);

      String filename = "ChiTietDonHang_" + orderId + ".xlsx";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", filename);
      headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

      return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
