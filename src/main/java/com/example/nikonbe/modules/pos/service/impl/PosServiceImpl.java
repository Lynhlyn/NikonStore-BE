package com.example.nikonbe.modules.pos.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.color_image.entity.ColorImage;
import com.example.nikonbe.modules.color_image.repository.ColorImageRepository;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.order_detail.mapper.OrderDetailMapper;
import com.example.nikonbe.modules.order_detail.repository.OrderDetailRepository;
import com.example.nikonbe.modules.order_history.entity.OrderHistory;
import com.example.nikonbe.modules.order_history.repository.OrderHistoryRepository;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.orders.mapper.OrderMapper;
import com.example.nikonbe.modules.orders.repository.OrderRepository;
import com.example.nikonbe.modules.orders.service.impl.OrderServiceImpl;
import com.example.nikonbe.modules.pos.dto.request.CompletePosOrderRequest;
import com.example.nikonbe.modules.pos.dto.request.CreatePosPendingOrderRequest;
import com.example.nikonbe.modules.pos.dto.request.UpdatePosPendingOrderRequest;
import com.example.nikonbe.modules.pos.dto.response.ListOrderPosResponse;
import com.example.nikonbe.modules.pos.dto.response.PosOrderDetailResponse;
import com.example.nikonbe.modules.pos.dto.response.PosOrderResponse;
import com.example.nikonbe.modules.pos.dto.response.ProductDetailPosResponse;
import com.example.nikonbe.modules.pos.service.interF.PosService;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product.mapper.ProductMapper;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.product_detail.repository.ProductDetailRepository;
import com.example.nikonbe.modules.promotion.service.interF.PromotionService;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import com.example.nikonbe.modules.voucher.repository.VoucherRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PosServiceImpl implements PosService {

  private final ProductDetailRepository productDetailRepository;
  private final ColorImageRepository colorImageRepository;
  private final PromotionService promotionService;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final OrderDetailMapper orderDetailMapper;
  private final StaffRepository staffRepository;
  private final OrderHistoryRepository orderHistoryRepository;
  private final CustomerRepository customerRepository;
  private final VoucherRepository voucherRepository;
  private final OrderServiceImpl orderServiceImpl;
  private final OrderDetailRepository orderDetailRepository;

  @Override
  public Page<ProductDetailPosResponse> getProductDetailsByProductId(
      Integer productId,
      String sku,
      Integer colorId,
      Integer capacityId,
      Status status,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Integer promotionId,
      Pageable pageable) {

    log.debug("Getting product details for product ID: {} with filters", productId);

    if (productId == null) {
      throw new IllegalArgumentException("Product ID không được để trống");
    }

    Page<ProductDetail> productDetails =
        productDetailRepository.findByFilters(
            productId, sku, colorId, capacityId, status, minPrice, maxPrice, promotionId, pageable);

    return productDetails.map(this::mapToProductDetailPosResponse);
  }

  @Override
  public ProductDetailPosResponse getProductDetailById(Integer id) {
    log.debug("Getting product detail by ID: {}", id);

    ProductDetail productDetail =
        productDetailRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", "id", id));

    return mapToProductDetailPosResponse(productDetail);
  }

  @Override
  public Page<ProductResponseDTO> getProducts(
      String keyword,
      Integer brandId,
      Integer categoryId,
      Integer materialId,
      Integer strapTypeId,
      Status status,
      Pageable pageable) {

    log.debug(
        "Getting products with filters - keyword: {}, brandId: {}, categoryId: {}, materialId: {}, strapTypeId: {}, status: {}",
        keyword,
        brandId,
        categoryId,
        materialId,
        strapTypeId,
        status);

    Page<Product> products =
        productRepository.findAllWithFilters(
            keyword,
            status != null ? status : Status.ACTIVE,
            categoryId,
            brandId,
            materialId,
            strapTypeId,
            pageable);

    return products.map(productMapper::toDto);
  }

  private ProductDetailPosResponse mapToProductDetailPosResponse(ProductDetail productDetail) {
    Integer availableStock =
        productDetail.getStock()
            - (productDetail.getReservedStock() != null ? productDetail.getReservedStock() : 0);

    String thumbnailImage = null;
    if (productDetail.getColor() != null && productDetail.getProduct() != null) {
      Optional<ColorImage> colorImageOpt =
          colorImageRepository.findByProductIdAndColorId(
              productDetail.getProduct().getId(), productDetail.getColor().getId());

      if (colorImageOpt.isPresent()) {
        thumbnailImage = colorImageOpt.get().getImageUrl();
      }
    }

    return ProductDetailPosResponse.builder()
        .id(productDetail.getId())
        .sku(productDetail.getSku())
        .stock(productDetail.getStock())
        .reservedStock(
            productDetail.getReservedStock() != null ? productDetail.getReservedStock() : 0)
        .availableStock(availableStock)
        .productName(
            productDetail.getProduct() != null ? productDetail.getProduct().getName() : null)
        .color(
            productDetail.getColor() != null ? mapToColorResponse(productDetail.getColor()) : null)
        .capacity(
            productDetail.getCapacity() != null
                ? mapToCapacityResponse(productDetail.getCapacity())
                : null)
        .price(productDetail.getPrice())
        .status(productDetail.getStatus())
        .promotion(
            productDetail.getPromotion() != null
                ? mapToPromotionResponse(productDetail.getPromotion())
                : null)
        .thumbnailImage(thumbnailImage)
        .build();
  }

  private com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO
      mapToColorResponse(com.example.nikonbe.modules.attributes.color.entity.Color color) {
    return com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO.builder()
        .id(color.getId())
        .name(color.getName())
        .hexCode(color.getHexCode())
        .status(color.getStatus())
        .build();
  }

  private com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO
      mapToCapacityResponse(
          com.example.nikonbe.modules.attributes.capacity.entity.Capacity capacity) {
    return com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO
        .builder()
        .id(capacity.getId())
        .name(capacity.getName())
        .status(capacity.getStatus())
        .build();
  }

  private com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO
      mapToPromotionResponse(com.example.nikonbe.modules.promotion.entity.Promotion promotion) {
    return com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO.builder()
        .id(promotion.getId())
        .name(promotion.getName())
        .code(promotion.getCode())
        .description(promotion.getDescription())
        .discountType(promotion.getDiscountType())
        .discountValue(promotion.getDiscountValue())
        .appliesTo(promotion.getAppliesTo())
        .appliedProduct(promotion.getAppliedProduct())
        .startDate(promotion.getStartDate())
        .endDate(promotion.getEndDate())
        .status(promotion.getStatus())
        .createdAt(promotion.getCreatedAt())
        .updatedAt(promotion.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional
  public ListOrderResponse createPendingPOSOrder(CreatePosPendingOrderRequest request) {
    Staff staff =
        staffRepository
            .findById(request.getStaffId())
            .orElseThrow(
                () -> new IllegalArgumentException("Staff not found: " + request.getStaffId()));
    int pendingOrderCount =
        orderRepository.countByStaffIdAndStatusAndOrderType(
            staff.getId(), Status.PENDING_PAYMENT, "IN_STORE");
    if (pendingOrderCount >= 5) {
      throw new ValidationException("Không được tạo quá 5 đơn hàng chờ ");
    }

    Customer customer = null;
    if (request.getCustomerId() != null) {
      customer =
          customerRepository
              .findById(request.getCustomerId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Customer not found: " + request.getCustomerId()));
    } else {
      customer =
          customerRepository
              .findById(1)
              .orElseThrow(() -> new IllegalArgumentException("Default customer not found"));
    }

    Order order = new Order();
    order.setCustomer(customer);
    order.setStaff(staff);
    order.setTotalAmount(BigDecimal.ZERO);
    order.setDiscount(BigDecimal.ZERO);
    order.setShippingFee(BigDecimal.ZERO);
    order.setStatus(Status.PENDING_PAYMENT);
    order.setOrderType("IN_STORE");
    order.setPaymentMethod(request.getPaymentMethod());
    order.setPaymentStatus(request.getPaymentStatus());
    order.setNote(request.getNote());
    order.setShippingAddress("Tại quầy");
    order.setTrackingNumber(orderServiceImpl.generateTrackingNumber());
    if (customer != null) {
      order.setRecipientName(customer.getFullName());
      order.setRecipientPhone(customer.getPhoneNumber());
      order.setRecipientEmail(customer.getEmail());
    }

    Order savedOrder = orderRepository.save(order);

    Integer customerId = savedOrder.getCustomer() != null ? savedOrder.getCustomer().getId() : null;
    OrderHistory orderHistory =
        orderServiceImpl.createOrderHistory(
            savedOrder,
            request.getStaffId(),
            customerId,
            null,
            Status.PENDING_PAYMENT,
            "Tạo đơn hàng POS chờ thanh toán");
    orderHistoryRepository.save(orderHistory);

    ListOrderResponse response = orderMapper.toCreateOrderResponse(savedOrder);
    response.setOrderDetails(List.of());
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ListOrderPosResponse> getPendingPOSOrders(
      Integer customerId, Integer staffId, Pageable pageable) {
    Page<Order> orders =
        orderRepository.findPendingPOSOrders(
            Status.PENDING_PAYMENT, "IN_STORE", customerId, staffId, pageable);
    return orders.map(
        order -> {
          ListOrderPosResponse response = new ListOrderPosResponse();
          response.setId(order.getId());
          response.setCustomer(
              order.getCustomer() != null ? mapToCustomerResponseDTO(order.getCustomer()) : null);
          response.setTotalAmount(order.getTotalAmount());
          response.setDiscount(order.getDiscount());
          response.setVoucherId(
              order.getVoucher() != null ? Math.toIntExact(order.getVoucher().getId()) : null);
          response.setPaymentMethod(order.getPaymentMethod());
          response.setPaymentStatus(order.getPaymentStatus());
          response.setNote(order.getNote());
          response.setStaff(
              order.getStaff() != null ? mapToStaffResponseDTO(order.getStaff()) : null);
          response.setOrderDetails(
              order.getOrderDetails() != null
                  ? order.getOrderDetails().stream()
                      .map(orderDetailMapper::toOrderProductResponse)
                      .toList()
                  : List.of());
          return response;
        });
  }

  private CustomerResponseDTO mapToCustomerResponseDTO(Customer customer) {
    return CustomerResponseDTO.builder()
        .id(customer.getId())
        .fullName(customer.getFullName())
        .email(customer.getEmail())
        .phoneNumber(customer.getPhoneNumber())
        .status(customer.getStatus())
        .build();
  }

  private StaffResponseDTO mapToStaffResponseDTO(Staff staff) {
    return StaffResponseDTO.builder()
        .id(staff.getId())
        .fullName(staff.getFullName())
        .email(staff.getEmail())
        .phoneNumber(staff.getPhoneNumber())
        .status(staff.getStatus())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public PosOrderResponse getPendingOrderById(Integer orderId) {
    log.debug("Getting pending POS order by ID: {}", orderId);

    Order order =
        orderRepository
            .findByIdWithDetails(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

    if (!Status.PENDING_PAYMENT.equals(order.getStatus())
        || !"IN_STORE".equals(order.getOrderType())) {
      throw new IllegalArgumentException(
          "Đơn hàng không phải là POS hoặc không trong trạng thái chờ thanh toán");
    }

    return mapToPosOrderResponse(order);
  }

  @Override
  @Transactional
  public PosOrderResponse updatePendingOrder(
      Integer orderId, UpdatePosPendingOrderRequest request) {
    log.debug("Updating pending POS order ID: {} with request: {}", orderId, request);

    Order order =
        orderRepository
            .findByIdWithDetails(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

    if (!Status.PENDING_PAYMENT.equals(order.getStatus())
        || !"IN_STORE".equals(order.getOrderType())) {
      throw new IllegalArgumentException("Chỉ có thể cập nhật đơn hàng POS đang chờ thanh toán");
    }

    try {
      updateBasicOrderInfo(order, request);

      if (request.getOrderDetails() != null) {
        updateOrderDetails(order, request.getOrderDetails());
      }

      recalculateOrderAmounts(order);

      Order savedOrder = orderRepository.save(order);

      OrderHistory orderHistory =
          orderServiceImpl.createOrderHistory(
              savedOrder,
              savedOrder.getStaff().getId(),
              null,
              Status.PENDING_PAYMENT,
              Status.PENDING_PAYMENT,
              "Cập nhật đơn hàng POS");
      orderHistoryRepository.save(orderHistory);

      log.info("Successfully updated POS order ID: {}", orderId);
      return mapToPosOrderResponse(savedOrder);

    } catch (Exception e) {
      log.error("Error updating POS order ID: {}", orderId, e);
      throw e;
    }
  }

  @Override
  @Transactional
  public PosOrderResponse completeOrder(Integer orderId, CompletePosOrderRequest request) {
    log.debug("Completing POS order ID: {} with request: {}", orderId, request);

    Order order =
        orderRepository
            .findByIdWithDetails(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

    if (!Status.PENDING_PAYMENT.equals(order.getStatus())
        || !"IN_STORE".equals(order.getOrderType())) {
      throw new IllegalArgumentException("Chỉ có thể hoàn tất đơn hàng POS đang chờ thanh toán");
    }

    if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
      throw new IllegalArgumentException("Không thể hoàn tất đơn hàng trống");
    }

    if (request.getVoucherId() != null) {
      validateAndApplyVoucher(order, request.getVoucherId());
    }

    order.setPaymentMethod(request.getPaymentMethod());
    order.setPaymentStatus("completed");
    order.setStatus(Status.COMPLETED);
    order.setNote(request.getOrderNote());

    recalculateOrderAmounts(order);

    validatePaymentAmount(
        order.getTotalAmount(), request.getAmountPaid(), request.getChangeAmount());

    if (order.getVoucher() != null) {
      Voucher voucher = order.getVoucher();
      Integer usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
      voucher.setUsedCount(usedCount + 1);
      voucherRepository.save(voucher);
    }

    Order savedOrder = orderRepository.save(order);

    OrderHistory orderHistory =
        orderServiceImpl.createOrderHistory(
            savedOrder,
            savedOrder.getStaff().getId(),
            null,
            Status.PENDING_PAYMENT,
            Status.COMPLETED,
            "Hoàn tất đơn hàng POS");
    orderHistoryRepository.save(orderHistory);

    for (OrderDetail detail : order.getOrderDetails()) {
      ProductDetail productDetail = detail.getProductDetail();
      productDetail.setStock(productDetail.getStock() - detail.getQuantity());
      if (productDetail.getReservedStock() != null) {
        productDetail.setReservedStock(productDetail.getReservedStock() - detail.getQuantity());
      }
      productDetailRepository.save(productDetail);
    }

    return mapToPosOrderResponse(savedOrder);
  }

  private void updateBasicOrderInfo(Order order, UpdatePosPendingOrderRequest request) {
    if (request.getCustomerId() != null) {
      Customer customer =
          customerRepository
              .findById(request.getCustomerId())
              .orElseThrow(
                  () -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
      order.setCustomer(customer);
      order.setRecipientName(customer.getFullName());
      order.setRecipientPhone(customer.getPhoneNumber());
      order.setRecipientEmail(customer.getEmail());
    }

    if (request.getPaymentMethod() != null) {
      order.setPaymentMethod(request.getPaymentMethod());
    }

    if (request.getPaymentStatus() != null) {
      order.setPaymentStatus(request.getPaymentStatus());
    }

    if (request.getNote() != null) {
      order.setNote(request.getNote());
    }
  }

  private void updateOrderDetails(
      Order order, List<UpdatePosPendingOrderRequest.OrderDetailItem> requestDetails) {

    if (order.getOrderDetails() == null) {
      order.setOrderDetails(new ArrayList<>());
    }

    Map<Integer, OrderDetail> existingDetailsMap =
        order.getOrderDetails().stream()
            .collect(
                Collectors.toMap(detail -> detail.getProductDetail().getId(), detail -> detail));

    Set<Integer> requestProductDetailIds =
        requestDetails.stream()
            .filter(item -> item.getQuantity() > 0)
            .map(UpdatePosPendingOrderRequest.OrderDetailItem::getProductDetailId)
            .collect(Collectors.toSet());

    List<OrderDetail> detailsToRemove =
        order.getOrderDetails().stream()
            .filter(detail -> !requestProductDetailIds.contains(detail.getProductDetail().getId()))
            .collect(Collectors.toList());

    if (!detailsToRemove.isEmpty()) {
      orderDetailRepository.deleteAll(detailsToRemove);
      order.getOrderDetails().removeAll(detailsToRemove);
    }

    for (UpdatePosPendingOrderRequest.OrderDetailItem item : requestDetails) {
      if (item.getQuantity() <= 0) {
        continue;
      }

      Integer productDetailId = item.getProductDetailId();
      OrderDetail existingDetail = existingDetailsMap.get(productDetailId);

      if (existingDetail != null) {
        updateExistingOrderDetail(existingDetail, item);
      } else {
        OrderDetail newOrderDetail = createOrderDetailFromItem(order, item);
        order.getOrderDetails().add(newOrderDetail);
      }
    }
  }

  private void updateExistingOrderDetail(
      OrderDetail existingDetail, UpdatePosPendingOrderRequest.OrderDetailItem item) {
    ProductDetail productDetail = existingDetail.getProductDetail();

    Integer currentReserved =
        productDetail.getReservedStock() != null ? productDetail.getReservedStock() : 0;
    Integer currentOrderQuantity = existingDetail.getQuantity();
    Integer newQuantity = item.getQuantity();

    Integer quantityChange = newQuantity - currentOrderQuantity;

    Integer availableStock = productDetail.getStock() - currentReserved + currentOrderQuantity;

    if (availableStock < newQuantity) {
      throw new IllegalArgumentException("Không đủ tồn kho cho sản phẩm");
    }

    if (quantityChange != 0) {
      productDetail.setReservedStock(currentReserved + quantityChange);
      productDetailRepository.save(productDetail);
    }

    BigDecimal unitPrice = calculatePromotionPrice(productDetail);
    BigDecimal originalPrice = productDetail.getPrice();
    BigDecimal discountPerUnit = originalPrice.subtract(unitPrice);

    existingDetail.setQuantity(newQuantity);
    existingDetail.setPrice(unitPrice);
    existingDetail.setDiscount(discountPerUnit.multiply(BigDecimal.valueOf(newQuantity)));
  }

  private OrderDetail createOrderDetailFromItem(
      Order order, UpdatePosPendingOrderRequest.OrderDetailItem item) {
    ProductDetail productDetail =
        productDetailRepository
            .findById(item.getProductDetailId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "ProductDetail", "id", item.getProductDetailId()));

    Integer currentReserved =
        productDetail.getReservedStock() != null ? productDetail.getReservedStock() : 0;
    Integer availableStock = productDetail.getStock() - currentReserved;

    if (availableStock < item.getQuantity()) {
      throw new IllegalArgumentException("Không đủ tồn kho cho sản phẩm");
    }

    productDetail.setReservedStock(currentReserved + item.getQuantity());
    productDetailRepository.save(productDetail);

    BigDecimal unitPrice = calculatePromotionPrice(productDetail);
    BigDecimal originalPrice = productDetail.getPrice();
    BigDecimal discountPerUnit = originalPrice.subtract(unitPrice);

    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrder(order);
    orderDetail.setProductDetail(productDetail);
    orderDetail.setQuantity(item.getQuantity());
    orderDetail.setPrice(unitPrice);
    orderDetail.setDiscount(discountPerUnit.multiply(BigDecimal.valueOf(item.getQuantity())));

    return orderDetail;
  }

  private BigDecimal calculatePromotionPrice(ProductDetail productDetail) {
    BigDecimal originalPrice = productDetail.getPrice();

    if (productDetail.getPromotion() == null) {
      return originalPrice;
    }

    LocalDateTime now = LocalDateTime.now();
    if (productDetail.getPromotion().getStartDate().isAfter(now)
        || productDetail.getPromotion().getEndDate().isBefore(now)
        || !Status.ACTIVE.equals(productDetail.getPromotion().getStatus())) {
      return originalPrice;
    }

    return promotionService.calculateDiscountedPrice(originalPrice, productDetail.getPromotion());
  }

  private void recalculateOrderAmounts(Order order) {
    if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
      order.setTotalAmount(BigDecimal.ZERO);
      order.setDiscount(BigDecimal.ZERO);
      return;
    }

    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal productDiscount = BigDecimal.ZERO;

    for (OrderDetail detail : order.getOrderDetails()) {
      BigDecimal detailSubtotal =
          detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
      subtotal = subtotal.add(detailSubtotal);

      BigDecimal detailDiscount =
          detail.getDiscount() != null ? detail.getDiscount() : BigDecimal.ZERO;
      productDiscount = productDiscount.add(detailDiscount);
    }

    BigDecimal voucherDiscount = BigDecimal.ZERO;
    if (order.getVoucher() != null) {
      voucherDiscount = calculateVoucherDiscount(subtotal, order.getVoucher());
    }

    BigDecimal totalDiscount = productDiscount.add(voucherDiscount);
    BigDecimal totalAmount = subtotal.subtract(voucherDiscount);

    order.setTotalAmount(totalAmount.max(BigDecimal.ZERO));
    order.setDiscount(totalDiscount);

    log.debug(
        "Recalculated order amounts - Subtotal: {}, ProductDiscount: {}, VoucherDiscount: {}, TotalAmount: {}",
        subtotal,
        productDiscount,
        voucherDiscount,
        totalAmount);
  }

  private BigDecimal calculateVoucherDiscount(BigDecimal subtotal, Voucher voucher) {
    if (voucher == null) return BigDecimal.ZERO;

    LocalDateTime now = LocalDateTime.now();
    if (voucher.getStartDate().isAfter(now)
        || voucher.getEndDate().isBefore(now)
        || voucher.getStatus() != Status.ACTIVE) {
      return BigDecimal.ZERO;
    }

    if (voucher.getMinOrderValue() != null && subtotal.compareTo(voucher.getMinOrderValue()) < 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal discount;
    if ("percentage".equals(voucher.getDiscountType())) {
      discount =
          subtotal
              .multiply(voucher.getDiscountValue())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

      if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
        discount = voucher.getMaxDiscount();
      }
    } else {
      discount = voucher.getDiscountValue();
    }

    return discount.min(subtotal);
  }

  private void validateAndApplyVoucher(Order order, Integer voucherId) {
    Voucher voucher =
        voucherRepository
            .findById(voucherId.longValue())
            .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", voucherId));

    BigDecimal orderValue = calculateOrderSubtotal(order);

    LocalDateTime now = LocalDateTime.now();
    if (voucher.getStartDate().isAfter(now) || voucher.getEndDate().isBefore(now)) {
      throw new IllegalArgumentException("Voucher đã hết hạn");
    }

    if (voucher.getStatus() != Status.ACTIVE) {
      throw new IllegalArgumentException("Voucher không còn hiệu lực");
    }

    if (voucher.getMinOrderValue() != null
        && orderValue.compareTo(voucher.getMinOrderValue()) < 0) {
      throw new IllegalArgumentException(
          "Giá trị đơn hàng phải tối thiểu "
              + voucher.getMinOrderValue()
              + " để sử dụng voucher này");
    }

    if (voucher.getUsedCount() != null
        && voucher.getQuantity() != null
        && voucher.getUsedCount() >= voucher.getQuantity()) {
      throw new IllegalArgumentException("Voucher đã hết số lượng");
    }

    order.setVoucher(voucher);
  }

  private void validatePaymentAmount(
      BigDecimal totalAmount, BigDecimal amountPaid, BigDecimal changeAmount) {
    if (amountPaid.compareTo(totalAmount) < 0) {
      throw new IllegalArgumentException("Số tiền thanh toán không đủ");
    }

    BigDecimal expectedChange = amountPaid.subtract(totalAmount);
    if (changeAmount != null && changeAmount.compareTo(expectedChange) != 0) {
      throw new IllegalArgumentException("Số tiền thừa không chính xác");
    }
  }

  private PosOrderResponse mapToPosOrderResponse(Order order) {
    PosOrderResponse.PosOrderResponseBuilder builder =
        PosOrderResponse.builder()
            .id(order.getId())
            .code(order.getTrackingNumber())
            .customer(
                order.getCustomer() != null ? mapToCustomerResponseDTO(order.getCustomer()) : null)
            .staff(order.getStaff() != null ? mapToStaffResponseDTO(order.getStaff()) : null)
            .paymentMethod(order.getPaymentMethod())
            .paymentStatus(order.getPaymentStatus())
            .status(order.getStatus())
            .note(order.getNote())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt());

    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
      BigDecimal subtotal =
          order.getOrderDetails().stream()
              .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal productDiscount =
          order.getOrderDetails().stream()
              .map(detail -> detail.getDiscount() != null ? detail.getDiscount() : BigDecimal.ZERO)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal voucherDiscount = BigDecimal.ZERO;
      if (order.getVoucher() != null) {
        voucherDiscount = calculateVoucherDiscount(subtotal, order.getVoucher());
      }

      builder
          .subtotal(subtotal)
          .productDiscount(productDiscount)
          .voucherDiscount(voucherDiscount)
          .totalDiscount(order.getDiscount())
          .totalAmount(order.getTotalAmount())
          .voucher(order.getVoucher() != null ? mapToVoucherResponseDTO(order.getVoucher()) : null)
          .orderDetails(
              order.getOrderDetails().stream().map(this::mapToPosOrderDetailResponse).toList());
    } else {
      builder
          .subtotal(BigDecimal.ZERO)
          .productDiscount(BigDecimal.ZERO)
          .voucherDiscount(BigDecimal.ZERO)
          .totalDiscount(BigDecimal.ZERO)
          .totalAmount(BigDecimal.ZERO)
          .orderDetails(new ArrayList<>());
    }

    return builder.build();
  }

  private PosOrderDetailResponse mapToPosOrderDetailResponse(OrderDetail orderDetail) {
    ProductDetail productDetail = orderDetail.getProductDetail();

    String thumbnailImage = null;
    if (productDetail.getColor() != null && productDetail.getProduct() != null) {
      Optional<ColorImage> colorImageOpt =
          colorImageRepository.findByProductIdAndColorId(
              productDetail.getProduct().getId(), productDetail.getColor().getId());

      if (colorImageOpt.isPresent()) {
        thumbnailImage = colorImageOpt.get().getImageUrl();
      }
    }

    BigDecimal totalAmount =
        orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity()));

    return PosOrderDetailResponse.builder()
        .id(orderDetail.getId())
        .productDetailId(productDetail.getId())
        .sku(productDetail.getSku())
        .productName(
            productDetail.getProduct() != null ? productDetail.getProduct().getName() : null)
        .color(
            productDetail.getColor() != null ? mapToColorResponse(productDetail.getColor()) : null)
        .capacity(
            productDetail.getCapacity() != null
                ? mapToCapacityResponse(productDetail.getCapacity())
                : null)
        .quantity(orderDetail.getQuantity())
        .price(productDetail.getPrice())
        .discount(orderDetail.getDiscount() != null ? orderDetail.getDiscount() : BigDecimal.ZERO)
        .totalAmount(totalAmount)
        .promotion(
            productDetail.getPromotion() != null
                ? mapToPromotionResponse(productDetail.getPromotion())
                : null)
        .thumbnailImage(thumbnailImage)
        .build();
  }

  private com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO
      mapToVoucherResponseDTO(Voucher voucher) {
    return com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO.builder()
        .id(voucher.getId().toString())
        .description(voucher.getDescription())
        .code(voucher.getCode())
        .discountType(voucher.getDiscountType())
        .discountValue(voucher.getDiscountValue())
        .minOrderValue(voucher.getMinOrderValue())
        .maxDiscount(voucher.getMaxDiscount())
        .startDate(voucher.getStartDate())
        .endDate(voucher.getEndDate())
        .status(voucher.getStatus())
        .build();
  }

  @Override
  @Transactional
  public PosOrderResponse cancelPendingOrder(
      Integer orderId, Integer staffId, String cancelReason) {
    log.debug(
        "Canceling POS order ID: {} by staff: {} with reason: {}", orderId, staffId, cancelReason);

    if (orderId == null) {
      log.error("Order ID is null when trying to cancel pending order");
      throw new IllegalArgumentException("Order ID cannot be null");
    }

    Order order =
        orderRepository
            .findByIdWithDetails(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

    if (!Status.PENDING_PAYMENT.equals(order.getStatus())
        || !"IN_STORE".equals(order.getOrderType())) {
      throw new IllegalArgumentException("Chỉ có thể hủy đơn hàng POS đang chờ thanh toán");
    }

    Staff staff = null;
    if (staffId != null) {
      staff =
          staffRepository
              .findById(staffId)
              .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));
    }

    try {
      boolean hasProducts = order.getOrderDetails() != null && !order.getOrderDetails().isEmpty();

      if (!hasProducts) {
        log.info("Order ID: {} has no products, deleting order completely", orderId);

        createCancelOrderHistory(order, staffId, cancelReason + " - Đơn hàng trống đã bị xóa");

        orderRepository.delete(order);

        PosOrderResponse response = new PosOrderResponse();
        response.setId(orderId);
        response.setStatus(Status.CANCELLED);
        response.setCode(order.getTrackingNumber());
        response.setCustomer(
            order.getCustomer() != null ? mapToCustomerResponseDTO(order.getCustomer()) : null);
        response.setStaff(staff != null ? mapToStaffResponseDTO(staff) : null);
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setNote("Đơn hàng trống đã bị xóa - Lý do: " + cancelReason);
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setSubtotal(BigDecimal.ZERO);
        response.setProductDiscount(BigDecimal.ZERO);
        response.setVoucherDiscount(BigDecimal.ZERO);
        response.setTotalDiscount(BigDecimal.ZERO);
        response.setTotalAmount(BigDecimal.ZERO);
        response.setOrderDetails(new ArrayList<>());

        log.info("Successfully deleted empty POS order ID: {} by staff: {}", orderId, staffId);
        return response;
      } else {
        restoreProductStock(order);

        if (order.getVoucher() != null) {
          Voucher voucher = order.getVoucher();
          Integer usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
          if (usedCount > 0) {
            voucher.setUsedCount(usedCount - 1);
            voucherRepository.save(voucher);
          }
        }

        order.setStatus(Status.CANCELLED);
        order.setNote(
            order.getNote() != null
                ? order.getNote() + " | Lý do hủy: " + cancelReason
                : "Lý do hủy: " + cancelReason);

        Order savedOrder = orderRepository.save(order);

        createCancelOrderHistory(savedOrder, staffId, cancelReason);

        log.info("Successfully cancelled POS order ID: {} by staff: {}", orderId, staffId);
        return mapToPosOrderResponse(savedOrder);
      }

    } catch (Exception e) {
      log.error("Error canceling POS order ID: {} by staff: {}", orderId, staffId, e);
      throw new RuntimeException("Lỗi khi hủy đơn hàng: " + e.getMessage(), e);
    }
  }

  private void restoreProductStock(Order order) {
    if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
      return;
    }

    List<ProductDetail> productsToUpdate = new ArrayList<>();

    for (OrderDetail orderDetail : order.getOrderDetails()) {
      ProductDetail productDetail = orderDetail.getProductDetail();

      Integer currentReserved =
          productDetail.getReservedStock() != null ? productDetail.getReservedStock() : 0;
      Integer quantityToRestore = orderDetail.getQuantity();

      Integer newReservedStock = Math.max(0, currentReserved - quantityToRestore);
      productDetail.setReservedStock(newReservedStock);

      productsToUpdate.add(productDetail);

      log.debug(
          "Restored stock for product detail ID: {}, quantity: {}, old reserved: {}, new reserved: {}",
          productDetail.getId(),
          quantityToRestore,
          currentReserved,
          newReservedStock);
    }

    if (!productsToUpdate.isEmpty()) {
      productDetailRepository.saveAll(productsToUpdate);
      log.info("Restored stock for {} product details", productsToUpdate.size());
    }
  }

  private BigDecimal calculateOrderSubtotal(Order order) {
    if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
      return BigDecimal.ZERO;
    }

    return order.getOrderDetails().stream()
        .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void createCancelOrderHistory(Order order, Integer staffId, String cancelReason) {
    OrderHistory orderHistory =
        orderServiceImpl.createOrderHistory(
            order,
            staffId,
            null,
            order.getStatus(),
            Status.CANCELLED,
            "Hủy đơn hàng POS - Lý do: " + cancelReason);
    orderHistoryRepository.save(orderHistory);
  }

  @Override
  public ProductDetailPosResponse searchProductDetailBySlug(String sku) {
    log.debug("Searching product detail by SKU: {}", sku);

    if (sku == null || sku.trim().isEmpty()) {
      throw new IllegalArgumentException("SKU không được để trống");
    }

    ProductDetail productDetail =
        productDetailRepository
            .findBySkuAndStatus(sku.trim(), Status.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", "sku", sku.trim()));

    return mapToProductDetailPosResponse(productDetail);
  }
}
