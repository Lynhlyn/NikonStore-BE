package com.example.nikonbe.api.client.orders;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.response.PaginationResponse;
import com.example.nikonbe.modules.orders.dto.request.CancelOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.CreateInstantOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.CreateOrderRequest;
import com.example.nikonbe.modules.orders.dto.response.GetOrderDetailResponse;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderStatusResponse;
import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.orders.service.interF.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.client.version}/orders")
@RequiredArgsConstructor
@Tag(name = "Client - Order_client API", description = "API đơn hàng dành cho khách hàng")
public class OrderController {

  @Autowired private OrderService orderService;

  @PostMapping()
  @Operation(
      summary = "Tạo đơn hàng mới (COD hoặc VNPay)",
      description =
          "Tạo đơn hàng từ giỏ hàng của khách hàng. Nếu paymentMethod=VNPAY sẽ trả về paymentUrl để FE redirect sang VNPay.")
  public ResponseEntity<ApiResponseDto<ListOrderResponse>> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    ListOrderResponse orderResponse = orderService.createOrder(request);
    ApiResponseDto<ListOrderResponse> response =
        ApiResponseDto.<ListOrderResponse>builder()
            .status(HttpStatus.CREATED.value())
            .message(
                orderResponse.getPaymentUrl() != null
                    ? "Order created, please pay via VNPay"
                    : "Order created successfully")
            .data(orderResponse)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/instant")
  @Operation(
      summary = "Tạo đơn hàng mua ngay",
      description = "Tạo đơn hàng mua ngay từ giỏ hàng của khách hàng hoặc khách vãng lai.")
  public ResponseEntity<ApiResponseDto<ListOrderResponse>> createInstantOrder(
      @Valid @RequestBody CreateInstantOrderRequest request) {
    ListOrderResponse orderResponse = orderService.CreateInstantOrder(request);
    ApiResponseDto<ListOrderResponse> response =
        ApiResponseDto.<ListOrderResponse>builder()
            .status(HttpStatus.CREATED.value())
            .message(
                orderResponse.getPaymentUrl() != null
                    ? "Instant order created, please pay via VNPay"
                    : "Instant order created successfully")
            .data(orderResponse)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping()
  @Operation(
      summary = "Lấy danh sách đơn hàng",
      description =
          "Lấy danh sách đơn hàng của khách hàng theo customerId với khả năng tìm kiếm theo trạng thái và ngày tạo")
  public ResponseEntity<ApiResponseDto<Iterable<OrderResponse>>> getOrdersByCustomerId(
      @RequestParam @Positive Integer customerId,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    Page<OrderResponse> orderPage =
        orderService.getOrdersByCustomerId(customerId, status, fromDate, toDate, page, size);
    PaginationResponse pagination =
        PaginationResponse.builder()
            .page(orderPage.getNumber())
            .size(orderPage.getSize())
            .totalElements(orderPage.getTotalElements())
            .totalPages(orderPage.getTotalPages())
            .build();
    ApiResponseDto<Iterable<OrderResponse>> response =
        ApiResponseDto.<Iterable<OrderResponse>>builder()
            .status(HttpStatus.OK.value())
            .message("Orders retrieved successfully")
            .data(orderPage.getContent())
            .pagination(pagination)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{orderId}")
  @Operation(
      summary = "Lấy chi tiết đơn hàng",
      description = "Lấy thông tin chi tiết của một đơn hàng theo orderId")
  public ResponseEntity<ApiResponseDto<GetOrderDetailResponse>> getOrderDetailById(
      @PathVariable @Positive Integer orderId) {
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
  @Operation(
      summary = "Hủy đơn hàng",
      description = "Hủy đơn hàng theo orderId (có thể do khách hàng hoặc nhân viên)")
  public ResponseEntity<ApiResponseDto<ListOrderResponse>> cancelOrder(
      @Valid @RequestBody CancelOrderRequest request) {
    ListOrderResponse order = orderService.cancelOrder(request);
    ApiResponseDto<ListOrderResponse> response =
        ApiResponseDto.<ListOrderResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Order cancelled successfully")
            .data(order)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/tracking/{trackingNumber}")
  @Operation(
      summary = "Theo dõi đơn hàng",
      description = "Theo dõi đơn hàng theo trackingNumber và email (bắt buộc)")
  public ResponseEntity<ApiResponseDto<GetOrderDetailResponse>> trackingOrder(
      @PathVariable @Valid String trackingNumber, @RequestParam String email) {
    try {
      GetOrderDetailResponse orderResponse = orderService.trackingOrder(trackingNumber, email);
      ApiResponseDto<GetOrderDetailResponse> response =
          ApiResponseDto.<GetOrderDetailResponse>builder()
              .status(HttpStatus.OK.value())
              .message("Order retrieved successfully")
              .data(orderResponse)
              .build();
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      ApiResponseDto<GetOrderDetailResponse> response =
          ApiResponseDto.<GetOrderDetailResponse>builder()
              .status(HttpStatus.BAD_REQUEST.value())
              .message(ex.getMessage())
              .data(null)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @GetMapping("/status/{trackingNumber}")
  @Operation(
      summary = "Kiểm tra trạng thái thanh toán",
      description = "API để frontend polling kiểm tra trạng thái đơn hàng")
  public ResponseEntity<ApiResponseDto<OrderStatusResponse>> checkOrderStatus(
      @PathVariable String trackingNumber) {

    Order order = orderService.getOrderByTrackingNumber(trackingNumber);
    if (order == null) {
      ApiResponseDto<OrderStatusResponse> response =
          ApiResponseDto.<OrderStatusResponse>builder()
              .status(HttpStatus.NOT_FOUND.value())
              .message("Order not found")
              .data(null)
              .build();
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    OrderStatusResponse statusResponse =
        OrderStatusResponse.builder()
            .trackingNumber(trackingNumber)
            .status(order.getStatus().getValue())
            .paymentStatus(order.getPaymentStatus())
            .build();

    ApiResponseDto<OrderStatusResponse> response =
        ApiResponseDto.<OrderStatusResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Order status retrieved successfully")
            .data(statusResponse)
            .build();

    return ResponseEntity.ok(response);
  }
}
