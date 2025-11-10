package com.example.nikonbe.modules.orders.service.interF;

import com.example.nikonbe.modules.orders.dto.request.CancelOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.CreateInstantOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.CreateOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.UpdateStatusOrderRequest;
import com.example.nikonbe.modules.orders.dto.response.GetOrderDetailResponse;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderAllResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderResponse;
import com.example.nikonbe.modules.orders.entity.Order;
import org.springframework.data.domain.Page;

public interface OrderService {
  Page<OrderResponse> getOrdersByCustomerId(
      Integer customerId, Integer status, String fromDate, String toDate, int page, int size);

  GetOrderDetailResponse getOrderDetailById(Integer orderId);

  Page<OrderAllResponse> getAllOrders(int page, int size);

  Page<OrderAllResponse> searchOrders(
      String keyword,
      String type,
      Integer status,
      String fromDate,
      String toDate,
      int page,
      int size);

  ListOrderResponse cancelOrder(CancelOrderRequest request);

  Order updateOrderStatus(UpdateStatusOrderRequest request);

  ListOrderResponse createOrder(CreateOrderRequest request);

  ListOrderResponse CreateInstantOrder(CreateInstantOrderRequest request);

  void completeOnlineOrder(String trackingNumber);

  GetOrderDetailResponse trackingOrder(String trackingNumber, String email);

  void cleanupOldPendingOrders();

  void autoCancelUnpaidOrders();

  Order getOrderByTrackingNumber(String trackingNumber);
}
