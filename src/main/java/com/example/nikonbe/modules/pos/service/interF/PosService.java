package com.example.nikonbe.modules.pos.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.pos.dto.request.CompletePosOrderRequest;
import com.example.nikonbe.modules.pos.dto.request.CreatePosPendingOrderRequest;
import com.example.nikonbe.modules.pos.dto.request.UpdatePosPendingOrderRequest;
import com.example.nikonbe.modules.pos.dto.response.ListOrderPosResponse;
import com.example.nikonbe.modules.pos.dto.response.PosOrderResponse;
import com.example.nikonbe.modules.pos.dto.response.ProductDetailPosResponse;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PosService {

  Page<ProductDetailPosResponse> getProductDetailsByProductId(
      Integer productId,
      String sku,
      Integer colorId,
      Integer capacityId,
      Status status,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Integer promotionId,
      Pageable pageable);

  ProductDetailPosResponse getProductDetailById(Integer id);

  Page<ProductResponseDTO> getProducts(
      String keyword,
      Integer brandId,
      Integer categoryId,
      Integer materialId,
      Integer strapTypeId,
      Status status,
      Pageable pageable);

  ListOrderResponse createPendingPOSOrder(CreatePosPendingOrderRequest request);

  Page<ListOrderPosResponse> getPendingPOSOrders(
      Integer customerId, Integer staffId, Pageable pageable);

  PosOrderResponse getPendingOrderById(Integer orderId);

  PosOrderResponse updatePendingOrder(Integer orderId, UpdatePosPendingOrderRequest request);

  PosOrderResponse completeOrder(Integer orderId, CompletePosOrderRequest request);

  PosOrderResponse cancelPendingOrder(Integer orderId, Integer staffId, String cancelReason);

  ProductDetailPosResponse searchProductDetailBySlug(String sku);

  String createVnpayQrPaymentUrl(Integer orderId, String ipAddr);

  String createVnpayQrCode(Integer orderId, String ipAddr, String context);

  void handleVnpayCallback(Map<String, String> params);

  void cleanupOldPendingOrders();
}
