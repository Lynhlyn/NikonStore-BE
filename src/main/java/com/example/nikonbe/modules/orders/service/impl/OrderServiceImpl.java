package com.example.nikonbe.modules.orders.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.cart.entity.Cart;
import com.example.nikonbe.modules.cart.repository.CartRepository;
import com.example.nikonbe.modules.cart_detail.entity.CartDetail;
import com.example.nikonbe.modules.cart_detail.repository.CartDetailRepository;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.order_detail.dto.response.OrderDetailReponse;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.order_detail.mapper.OrderDetailMapper;
import com.example.nikonbe.modules.order_detail.repository.OrderDetailRepository;
import com.example.nikonbe.modules.order_history.entity.OrderHistory;
import com.example.nikonbe.modules.order_history.repository.OrderHistoryRepository;
import com.example.nikonbe.modules.orders.dto.request.CancelOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.CreateInstantOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.CreateOrderRequest;
import com.example.nikonbe.modules.orders.dto.request.UpdateStatusOrderRequest;
import com.example.nikonbe.modules.orders.dto.response.GetOrderDetailResponse;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderAllResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderResponse;
import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.orders.mapper.OrderMapper;
import com.example.nikonbe.modules.orders.repository.OrderRepository;
import com.example.nikonbe.modules.orders.service.interF.OrderService;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.product_detail.repository.ProductDetailRepository;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import com.example.nikonbe.modules.voucher.repository.VoucherRepository;
import com.example.nikonbe.modules.vnpay.service.interF.VNPayService;
import com.example.nikonbe.security.service.mail.EmailService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final OrderMapper orderMapper;
  private final OrderDetailMapper orderDetailMapper;
  private final StaffRepository staffRepository;
  private final OrderHistoryRepository orderHistoryRepository;
  private final CustomerRepository customerRepository;
  private final CartRepository cartRepository;
  private final CartDetailRepository cartDetailRepository;
  private final ProductDetailRepository productDetailRepository;
  private final VoucherRepository voucherRepository;
  private final EmailService emailService;
  private final VNPayService vnPayService;

  @Override
  @Transactional(readOnly = true)
  public Page<OrderResponse> getOrdersByCustomerId(
      Integer customerId, Integer status, String fromDate, String toDate, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Status statusEnum = null;
    if (status != null) {
      statusEnum = Status.fromValue(status);
    }

    LocalDate from = null;
    LocalDate to = null;
    if (fromDate != null && !fromDate.isEmpty()) {
      from = LocalDate.parse(fromDate);
    }
    if (toDate != null && !toDate.isEmpty()) {
      to = LocalDate.parse(toDate);
    }

    Page<Order> orders =
        getOrdersByCustomerIdWithFilters(customerId, statusEnum, from, to, pageable);
    return orders.map(orderMapper::toOrderResponse);
  }

  private Page<Order> getOrdersByCustomerIdWithFilters(
      Integer customerId, Status status, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
    if (status == null && fromDate == null && toDate == null) {
      return orderRepository.findByCustomer_Id(customerId, pageable);
    }

    if (status != null && fromDate == null && toDate == null) {
      return orderRepository.findByCustomer_IdAndStatus(customerId, status, pageable);
    }

    if (status == null && fromDate != null && toDate != null) {
      return orderRepository.findByCustomer_IdAndCreatedDateBetween(
          customerId, fromDate, toDate, pageable);
    }

    if (status != null && fromDate != null && toDate != null) {
      return orderRepository.findByCustomer_IdAndStatusAndCreatedDateBetween(
          customerId, status, fromDate, toDate, pageable);
    }

    return orderRepository.findByCustomer_Id(customerId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public GetOrderDetailResponse getOrderDetailById(Integer orderId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithDetails(orderId);

    List<OrderDetailReponse> orderDetailList =
        orderDetails.stream().map(orderDetailMapper::toOrderProductResponse).toList();
    GetOrderDetailResponse getOrderDetailResponse = orderMapper.toGetOrderDetailResponse(order);
    getOrderDetailResponse.setOrderDetails(orderDetailList);

    return getOrderDetailResponse;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<OrderAllResponse> getAllOrders(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return orderRepository.findAll(pageable).map(orderMapper::toOrderAllResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<OrderAllResponse> searchOrders(
      String keyword,
      String type,
      Integer status,
      String fromDate,
      String toDate,
      int page,
      int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Status statusEnum = null;
    if (status != null) {
      statusEnum = Status.fromValue(status);
    }
    LocalDate from = null;
    LocalDate to = null;
    if (fromDate != null && !fromDate.isEmpty()) {
      from = LocalDate.parse(fromDate);
    }
    if (toDate != null && !toDate.isEmpty()) {
      to = LocalDate.parse(toDate);
    }
    Page<Order> orders =
        orderRepository.searchOrders(keyword, type, statusEnum, from, to, pageable);
    return orders.map(orderMapper::toOrderAllResponse);
  }

  @Override
  @Transactional
  public ListOrderResponse cancelOrder(CancelOrderRequest request) {
    Order order =
        orderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

    boolean isStaff = request.getStaffId() != null;
    if (isStaff) {
      staffRepository
          .findById(request.getStaffId())
          .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", request.getStaffId()));
    } else {
      if (order.getCustomer() == null
          || !order.getCustomer().getId().equals(request.getCustomerId())) {
        throw new IllegalArgumentException(
            "Order does not belong to customer with ID: " + request.getCustomerId());
      }
    }

    if (!isOrderCancellable(order, isStaff)) {
      throw new IllegalStateException(
          "Order cannot be cancelled in its current state: " + order.getStatus());
    }
    Status statusBefore = order.getStatus();
    order.setStatus(Status.CANCELLED);
    Status statusAfter = order.getStatus();

    if (statusBefore == Status.PENDING_CONFIRMATION) {
      updateReservedStockOnCancellation(order.getId());
    }

    String cancellationNote =
        createCancellationNote(order, request.getReason(), request.getStaffId());
    String currentNote = order.getNote();
    if (currentNote != null && !currentNote.trim().isEmpty()) {
      order.setNote(currentNote + " - " + cancellationNote);
    } else {
      order.setNote(cancellationNote);
    }

    OrderHistory orderHistory =
        createOrderHistory(
            order,
            request.getStaffId(),
            request.getCustomerId(),
            statusBefore,
            statusAfter,
            cancellationNote);
    orderHistoryRepository.save(orderHistory);

    Order cancelledOrder = orderRepository.save(order);

    try {
      emailService.sendOrderCancelledEmail(
          cancelledOrder.getRecipientEmail(),
          cancelledOrder.getRecipientName(),
          cancelledOrder.getTrackingNumber(),
          request.getReason());
    } catch (Exception e) {
      log.error("Error sending cancellation email: {}", e.getMessage());
    }

    List<OrderDetail> orderDetails =
        orderDetailRepository.findByOrderIdWithDetails(cancelledOrder.getId());

    BigDecimal finalAmount =
        calculateFinalAmount(
            cancelledOrder.getTotalAmount(),
            cancelledOrder.getDiscount(),
            cancelledOrder.getShippingFee());

    return buildCreateOrderResponseOptimized(
        cancelledOrder, cancelledOrder.getCustomer(), finalAmount, orderDetails);
  }

  private void updateReservedStockOnCancellation(Integer orderId) {
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithDetails(orderId);
    List<ProductDetail> productsToUpdate = new ArrayList<>();

    for (OrderDetail orderDetail : orderDetails) {
      ProductDetail productDetail = orderDetail.getProductDetail();
      Integer quantity = orderDetail.getQuantity();
      if (productDetail.getReservedStock() != null
          && productDetail.getReservedStock() >= quantity) {
        productDetail.setReservedStock(productDetail.getReservedStock() - quantity);
        productsToUpdate.add(productDetail);
      }
    }

    if (!productsToUpdate.isEmpty()) {
      productDetailRepository.saveAll(productsToUpdate);
    }
  }

  @Override
  @Transactional
  public Order updateOrderStatus(UpdateStatusOrderRequest request) {
    if (request.getStaffId() != null) {
      staffRepository
          .findById(request.getStaffId())
          .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", request.getStaffId()));
    }

    Order order =
        orderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

    Status statusBefore = order.getStatus();

    if (statusBefore == Status.PENDING_CONFIRMATION) {
      validateStockBeforeConfirm(order);
    }

    Status statusAfter = Status.fromValue(request.getAfterStatus());
    String reason = request.getReason();
    String noteOrder = order.getNote();
    if (statusAfter == Status.FAILED_DELIVERY) {
      order.setNote(noteOrder + " - " + reason);
    }
    order.setStatus(statusAfter);
    if (statusAfter == Status.COMPLETED && "COD".equalsIgnoreCase(order.getPaymentMethod())) {
      order.setPaymentStatus("completed");
    }
    if (statusAfter == Status.CONFIRMED) {
      updateStockandReverseReservedStock(request.getOrderId());
    }
    if (statusAfter == Status.FAILED_DELIVERY) {
      updateStockOnCancellation(request.getOrderId());
    }

    OrderHistory orderHistory =
        createOrderHistory(order, request.getStaffId(), null, statusBefore, statusAfter, reason);
    orderHistoryRepository.save(orderHistory);

    sendOrderStatusEmail(order, statusAfter, reason);

    return orderRepository.save(order);
  }

  private void validateStockBeforeConfirm(Order order) {
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithDetails(order.getId());
    List<String> insufficientProducts = new ArrayList<>();
    for (OrderDetail orderDetail : orderDetails) {
      ProductDetail productDetail = orderDetail.getProductDetail();
      if (orderDetail.getQuantity() > productDetail.getStock()) {
        insufficientProducts.add(
            "Sản phẩm ID: "
                + productDetail.getId()
                + " chỉ còn "
                + productDetail.getStock()
                + " sản phẩm, cần "
                + orderDetail.getQuantity());
      }
    }
    if (!insufficientProducts.isEmpty()) {
      throw new IllegalStateException(
          "Không đủ hàng cho các sản phẩm: " + String.join("; ", insufficientProducts));
    }
  }

  public void updateStockandReverseReservedStock(Integer idOrder) {
    Order order =
        orderRepository
            .findById(idOrder)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", idOrder));

    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithDetails(idOrder);
    List<ProductDetail> productsToUpdate = new ArrayList<>();
    for (OrderDetail orderDetail : orderDetails) {
      ProductDetail productDetail = orderDetail.getProductDetail();
      Integer quantity = orderDetail.getQuantity();
      if (productDetail.getStock() >= quantity) {
        productDetail.setStock(productDetail.getStock() - quantity);
        if (productDetail.getReservedStock() != null
            && productDetail.getReservedStock() >= quantity) {
          productDetail.setReservedStock(productDetail.getReservedStock() - quantity);
        }
        productsToUpdate.add(productDetail);
      }
    }
    if (!productsToUpdate.isEmpty()) {
      productDetailRepository.saveAll(productsToUpdate);
    }
  }

  private void updateStockOnCancellation(Integer orderId) {
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithDetails(orderId);
    List<ProductDetail> productsToUpdate = new ArrayList<>();

    for (OrderDetail orderDetail : orderDetails) {
      ProductDetail productDetail = orderDetail.getProductDetail();
      Integer quantity = orderDetail.getQuantity();
      productDetail.setStock(productDetail.getStock() + quantity);
      productsToUpdate.add(productDetail);
    }

    if (!productsToUpdate.isEmpty()) {
      productDetailRepository.saveAll(productsToUpdate);
    }
  }

  @Override
  @Transactional
  public ListOrderResponse createOrder(CreateOrderRequest request) {
    Customer customer = validateAndGetCustomer(request.getCustomerId(), request.getCookieId());
    Integer cartId = getCartId(request, customer);
    List<OrderDetail> orderDetails = new ArrayList<>();
    BigDecimal totalAmount = calculateOrderDetails(request, cartId, orderDetails);
    BigDecimal finalAmount =
        calculateFinalAmount(totalAmount, request.getDiscount(), request.getShippingFee());

    Order savedOrder = createAndSaveOrder(customer, totalAmount, request);
    String paymentUrl = null;

    if (request.getVoucherId() != null) {
      Voucher voucher = voucherRepository.findById(request.getVoucherId()).orElse(null);
      if (voucher != null) {
        savedOrder.setVoucher(voucher);
      }
    }

    saveOrderDetailsBatch(orderDetails, savedOrder);
    removeItemsFromCartBatch(request.getCartItems());

    if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
      savedOrder.setStatus(Status.PENDING_PAYMENT);
      savedOrder.setPaymentStatus("PENDING");
      orderHistoryRepository.save(
          createOrderHistory(
              savedOrder,
              null,
              customer.getId(),
              null,
              Status.PENDING_PAYMENT,
              "Đơn hàng online được tạo, chờ thanh toán"));
      paymentUrl =
          vnPayService.createPaymentUrl(
              finalAmount.longValue(),
              savedOrder.getTrackingNumber(),
              request.getIpAddress() != null ? request.getIpAddress() : "127.0.0.1",
              savedOrder.getId().toString());
    } else {
      savedOrder.setStatus(Status.PENDING_CONFIRMATION);
      savedOrder.setPaymentStatus("PENDING");
      createOrderHistoryRecord(savedOrder, customer);
    }
    orderRepository.save(savedOrder);

    try {
      BigDecimal discountForEmail =
          savedOrder.getDiscount() != null ? savedOrder.getDiscount() : BigDecimal.ZERO;
      BigDecimal shippingFeeForEmail =
          savedOrder.getShippingFee() != null ? savedOrder.getShippingFee() : BigDecimal.ZERO;
      BigDecimal orderTotal =
          savedOrder.getTotalAmount().subtract(discountForEmail).add(shippingFeeForEmail);

      if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
        emailService.sendOrderPendingPaymentEmail(
            request.getRecipientEmail(),
            request.getRecipientName(),
            savedOrder.getTrackingNumber(),
            orderTotal,
            paymentUrl);
      } else {
        emailService.sendOrderPendingConfirmationEmail(
            request.getRecipientEmail(),
            request.getRecipientName(),
            savedOrder.getTrackingNumber(),
            orderTotal);
      }
    } catch (Exception e) {
      log.error("Error sending order email: {}", e.getMessage());
    }

    ListOrderResponse response =
        buildCreateOrderResponseOptimized(savedOrder, customer, finalAmount, orderDetails);
    response.setPaymentUrl(paymentUrl);
    return response;
  }

  @Override
  @Transactional
  public ListOrderResponse CreateInstantOrder(CreateInstantOrderRequest request) {
    Customer customer = validateAndGetCustomer(request.getCustomerId(), request.getCookieId());
    List<OrderDetail> orderDetails = new ArrayList<>();
    BigDecimal totalAmount = calculateInstantOrderDetails(request, orderDetails);
    BigDecimal finalAmount =
        calculateFinalAmount(totalAmount, request.getDiscount(), request.getShippingFee());

    Order savedOrder = createAndSaveInstantOrder(customer, totalAmount, request);

    if (request.getVoucherId() != null) {
      Voucher voucher = voucherRepository.findById(request.getVoucherId()).orElse(null);
      if (voucher != null) {
        savedOrder.setVoucher(voucher);
      }
    }

    saveOrderDetailsBatch(orderDetails, savedOrder);
    String paymentUrl = null;

    if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
      savedOrder.setStatus(Status.PENDING_PAYMENT);
      savedOrder.setPaymentStatus("PENDING");
      orderHistoryRepository.save(
          createOrderHistory(
              savedOrder,
              null,
              customer.getId(),
              null,
              Status.PENDING_PAYMENT,
              "Đơn hàng mua ngay được tạo, chờ thanh toán"));
      paymentUrl =
          vnPayService.createPaymentUrl(
              finalAmount.longValue(),
              savedOrder.getTrackingNumber(),
              request.getIpAddress() != null ? request.getIpAddress() : "127.0.0.1",
              savedOrder.getId().toString());
    } else {
      savedOrder.setStatus(Status.PENDING_CONFIRMATION);
      savedOrder.setPaymentStatus("PENDING");
      createOrderHistoryRecord(savedOrder, customer);
    }

    orderRepository.save(savedOrder);

    try {
      BigDecimal discountForEmail =
          savedOrder.getDiscount() != null ? savedOrder.getDiscount() : BigDecimal.ZERO;
      BigDecimal shippingFeeForEmail =
          savedOrder.getShippingFee() != null ? savedOrder.getShippingFee() : BigDecimal.ZERO;
      BigDecimal orderTotal =
          savedOrder.getTotalAmount().subtract(discountForEmail).add(shippingFeeForEmail);

      if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
        emailService.sendOrderPendingPaymentEmail(
            request.getRecipientEmail(),
            request.getRecipientName(),
            savedOrder.getTrackingNumber(),
            orderTotal,
            paymentUrl);
      } else {
        emailService.sendOrderPendingConfirmationEmail(
            request.getRecipientEmail(),
            request.getRecipientName(),
            savedOrder.getTrackingNumber(),
            orderTotal);
      }
    } catch (Exception e) {
      log.error("Error sending order email: {}", e.getMessage());
    }

    ListOrderResponse response =
        buildCreateOrderResponseOptimized(savedOrder, customer, finalAmount, orderDetails);
    response.setPaymentUrl(paymentUrl);
    return response;
  }

  @Override
  @Transactional
  public void completeOnlineOrder(String trackingNumber) {
    Order order =
        orderRepository
            .findByTrackingNumber(trackingNumber)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order", "trackingNumber", trackingNumber));

    if (order.getStatus() == Status.PENDING_PAYMENT) {
      order.setStatus(Status.PENDING_CONFIRMATION);
      order.setPaymentStatus("completed");

      try {
        BigDecimal orderTotal =
            order
                .getTotalAmount()
                .add(order.getShippingFee())
                .subtract(order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO);

        emailService.sendOrderPendingConfirmationEmail(
            order.getRecipientEmail(),
            order.getRecipientName(),
            order.getTrackingNumber(),
            orderTotal);
      } catch (Exception e) {
        log.error("Error sending email: {}", e.getMessage());
      }

      OrderHistory orderHistory =
          createOrderHistory(
              order,
              null,
              order.getCustomer() != null ? order.getCustomer().getId() : null,
              Status.PENDING_PAYMENT,
              Status.PENDING_CONFIRMATION,
              "Thanh toán thành công. Đơn hàng chờ xác nhận.");
      orderHistoryRepository.save(orderHistory);

      orderRepository.save(order);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public GetOrderDetailResponse trackingOrder(String trackingNumber, String email) {
    Order order =
        orderRepository
            .findByTrackingNumber(trackingNumber)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order", "trackingNumber", trackingNumber));

    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email là bắt buộc để xem thông tin đơn hàng");
    }

    if (!email.trim().equalsIgnoreCase(order.getRecipientEmail().trim())) {
      throw new IllegalArgumentException(
          "Email không khớp với đơn hàng. Vui lòng kiểm tra lại email đã đặt hàng.");
    }

    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithDetails(order.getId());

    List<OrderDetailReponse> orderDetailResponses =
        orderDetails.stream().map(orderDetailMapper::toOrderProductResponse).toList();

    GetOrderDetailResponse response = orderMapper.toGetOrderDetailResponse(order);
    response.setOrderDetails(orderDetailResponses);
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderByTrackingNumber(String trackingNumber) {
    return orderRepository.findByTrackingNumber(trackingNumber).orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public String getOrderEmailByTrackingNumber(String trackingNumber) {
    Order order = orderRepository.findByTrackingNumber(trackingNumber).orElse(null);
    return order != null ? order.getRecipientEmail() : null;
  }

  private Customer validateAndGetCustomer(Integer customerId, String cookieId) {
    if (customerId != null) {
      return customerRepository
          .findById(customerId)
          .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    } else if (cookieId != null) {
      Customer guestCustomer = customerRepository.findById(1).orElse(null);
      if (guestCustomer == null) {
        throw new ResourceNotFoundException("Customer", "id", 1);
      }
      return guestCustomer;
    } else {
      throw new IllegalArgumentException("Cần cung cấp customerId hoặc cookieId");
    }
  }

  private Integer getCartId(CreateOrderRequest request, Customer customer) {
    if (request.getCustomerId() != null) {
      Cart cart =
          cartRepository
              .findByCustomer_Id(request.getCustomerId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("Cart", "customerId", request.getCustomerId()));
      return cart.getId();
    } else {
      Cart cart =
          cartRepository
              .findByCookieId(request.getCookieId())
              .orElseThrow(
                  () -> new ResourceNotFoundException("Cart", "cookieId", request.getCookieId()));
      return cart.getId();
    }
  }

  private BigDecimal calculateOrderDetails(
      CreateOrderRequest request, Integer cartId, List<OrderDetail> orderDetails) {
    BigDecimal totalAmount = BigDecimal.ZERO;
    List<ProductDetail> productsToUpdate = new ArrayList<>();

    for (CreateOrderRequest.CartItemRequest item : request.getCartItems()) {
      CartDetail cartDetail = validateCartDetail(item.getCartdetailId(), cartId);
      ProductDetail productDetail = cartDetail.getProductDetail();

      validateQuantity(item.getQuantity(), productDetail);

      BigDecimal finalPrice = cartDetail.getPrice();
      BigDecimal itemTotal = finalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

      totalAmount = totalAmount.add(itemTotal);

      updateProductStockForBatch(productDetail, item.getQuantity());
      productsToUpdate.add(productDetail);

      OrderDetail orderDetail =
          createOrderDetail(productDetail, item.getQuantity(), finalPrice, BigDecimal.ZERO);
      orderDetails.add(orderDetail);
    }

    if (!productsToUpdate.isEmpty()) {
      productDetailRepository.saveAll(productsToUpdate);
    }

    return totalAmount;
  }

  private CartDetail validateCartDetail(Integer cartDetailId, Integer cartId) {
    CartDetail cartDetail =
        cartDetailRepository
            .findById(cartDetailId)
            .orElseThrow(() -> new ResourceNotFoundException("CartDetail", "id", cartDetailId));

    if (!cartDetail.getCart().getId().equals(cartId)) {
      throw new IllegalArgumentException("Chi tiết giỏ hàng không thuộc giỏ hàng của khách hàng");
    }

    return cartDetail;
  }

  private void validateQuantity(Integer quantity, ProductDetail productDetail) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
    }
    if (productDetail.getStock() < quantity) {
      throw new IllegalArgumentException(
          "Không đủ hàng trong kho cho sản phẩm: " + productDetail.getId());
    }
  }

  private void updateProductStockForBatch(ProductDetail productDetail, Integer quantity) {
    if (productDetail.getReservedStock() == null) {
      productDetail.setReservedStock(0);
    }
    productDetail.setReservedStock(productDetail.getReservedStock() + quantity);
  }

  private OrderDetail createOrderDetail(
      ProductDetail productDetail,
      Integer quantity,
      BigDecimal finalPrice,
      BigDecimal discountAmount) {
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setProductDetail(productDetail);
    orderDetail.setQuantity(quantity);
    orderDetail.setPrice(finalPrice);
    orderDetail.setDiscount(discountAmount);
    return orderDetail;
  }

  private BigDecimal calculateFinalAmount(
      BigDecimal totalAmount, BigDecimal discount, BigDecimal shippingFee) {
    BigDecimal voucherdiscount = discount != null ? discount : BigDecimal.ZERO;
    BigDecimal finalShippingFee = shippingFee != null ? shippingFee : BigDecimal.ZERO;

    BigDecimal finalAmount = totalAmount.subtract(voucherdiscount).add(finalShippingFee);
    return finalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalAmount;
  }

  private Order createAndSaveOrder(
      Customer customer, BigDecimal totalAmount, CreateOrderRequest request) {
    Order order = new Order();
    order.setCustomer(customer);
    order.setTotalAmount(totalAmount);
    order.setDiscount(request.getDiscount());
    if (request.getVoucherId() != null) {
      Voucher voucher = voucherRepository.findById(request.getVoucherId()).orElse(null);
      order.setVoucher(voucher);
    }
    order.setStatus(Status.PENDING_CONFIRMATION);
    order.setNote(request.getNotes());
    order.setShippingAddress(request.getShippingAddress());
    order.setPaymentMethod(request.getPaymentMethod());
    order.setRecipientEmail(request.getRecipientEmail());
    order.setRecipientName(request.getRecipientName());
    order.setRecipientPhone(request.getRecipientPhone());
    order.setPaymentStatus("PENDING");
    order.setShippingFee(
        request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO);
    order.setOrderType(request.getOrderType() != null ? request.getOrderType() : "ONLINE");
    order.setTrackingNumber(generateTrackingNumber());

    return orderRepository.save(order);
  }

  private Order createAndSaveInstantOrder(
      Customer customer, BigDecimal totalAmount, CreateInstantOrderRequest request) {
    Order order = new Order();
    order.setCustomer(customer);
    order.setTotalAmount(totalAmount);
    order.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
    order.setStatus(Status.PENDING_CONFIRMATION);
    order.setNote(request.getNotes());
    order.setShippingAddress(request.getShippingAddress());
    order.setPaymentMethod(request.getPaymentMethod());
    order.setRecipientEmail(request.getRecipientEmail());
    order.setRecipientName(request.getRecipientName());
    order.setRecipientPhone(request.getRecipientPhone());
    order.setPaymentStatus("PENDING");
    order.setShippingFee(
        request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO);
    order.setOrderType(request.getOrderType() != null ? request.getOrderType() : "ONLINE");
    order.setTrackingNumber(generateTrackingNumber());

    return orderRepository.save(order);
  }

  private BigDecimal calculateInstantOrderDetails(
      CreateInstantOrderRequest request, List<OrderDetail> orderDetails) {
    BigDecimal totalAmount = BigDecimal.ZERO;
    List<ProductDetail> productsToUpdate = new ArrayList<>();

    for (CreateInstantOrderRequest.InstantOrderItemRequest item : request.getItems()) {
      ProductDetail productDetail =
          productDetailRepository
              .findById(item.getProductDetailId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "ProductDetail", "id", item.getProductDetailId()));

      validateQuantity(item.getQuantity(), productDetail);

      BigDecimal originalPrice = productDetail.getPrice();
      BigDecimal finalPrice = originalPrice;
      BigDecimal itemTotal = finalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

      totalAmount = totalAmount.add(itemTotal);

      updateProductStockForBatch(productDetail, item.getQuantity());
      productsToUpdate.add(productDetail);

      OrderDetail orderDetail =
          createOrderDetail(productDetail, item.getQuantity(), finalPrice, BigDecimal.ZERO);
      orderDetails.add(orderDetail);
    }

    if (!productsToUpdate.isEmpty()) {
      productDetailRepository.saveAll(productsToUpdate);
    }

    return totalAmount;
  }

  private void saveOrderDetailsBatch(List<OrderDetail> orderDetails, Order savedOrder) {
    List<OrderDetail> orderDetailsToSave = new ArrayList<>();
    for (OrderDetail orderDetail : orderDetails) {
      orderDetail.setOrder(savedOrder);
      orderDetailsToSave.add(orderDetail);
    }
    orderDetailRepository.saveAll(orderDetailsToSave);
  }

  private void removeItemsFromCartBatch(List<CreateOrderRequest.CartItemRequest> cartItems) {
    List<Integer> cartDetailIds =
        cartItems.stream().map(CreateOrderRequest.CartItemRequest::getCartdetailId).toList();
    cartDetailRepository.deleteAllById(cartDetailIds);
  }

  private void createOrderHistoryRecord(Order savedOrder, Customer customer) {
    OrderHistory orderHistory =
        createOrderHistory(
            savedOrder,
            null,
            customer.getId(),
            null,
            Status.PENDING_CONFIRMATION,
            "Đơn hàng được tạo mới");
    orderHistoryRepository.save(orderHistory);
  }

  private ListOrderResponse buildCreateOrderResponseOptimized(
      Order savedOrder, Customer customer, BigDecimal finalAmount, List<OrderDetail> orderDetails) {
    List<OrderDetailReponse> orderDetailResponses =
        orderDetails.stream().map(orderDetailMapper::toOrderProductResponse).toList();

    return ListOrderResponse.builder()
        .orderId(savedOrder.getId())
        .orderStatus(savedOrder.getStatus().getValue())
        .orderDate(
            savedOrder.getCreatedAt() != null
                ? savedOrder
                    .getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null)
        .totalAmount(savedOrder.getTotalAmount())
        .discount(savedOrder.getDiscount())
        .shippingFee(savedOrder.getShippingFee())
        .paymentMethod(savedOrder.getPaymentMethod())
        .paymentStatus(savedOrder.getPaymentStatus())
        .shippingAddress(savedOrder.getShippingAddress())
        .note(savedOrder.getNote())
        .trackingNumber(savedOrder.getTrackingNumber())
        .customerName(savedOrder.getRecipientName())
        .customerEmail(savedOrder.getRecipientEmail())
        .customerPhone(savedOrder.getRecipientPhone())
        .orderType(savedOrder.getOrderType())
        .finalAmount(finalAmount)
        .customerId(customer != null ? customer.getId() : null)
        .staffId(savedOrder.getStaff() != null ? savedOrder.getStaff().getId() : null)
        .orderDetails(orderDetailResponses)
        .build();
  }

  public OrderHistory createOrderHistory(
      Order order,
      Integer staffId,
      Integer customerId,
      Status statusBefore,
      Status statusAfter,
      String note) {
    OrderHistory orderHistory = new OrderHistory();
    orderHistory.setOrder(order);
    orderHistory.setChangeByType(
        staffId != null
            ? UserRole.staff
            : (customerId != null ? UserRole.customer : UserRole.system));
    orderHistory.setStaff(staffId != null ? staffRepository.findById(staffId).orElse(null) : null);
    orderHistory.setCustomer(customerId != null ? order.getCustomer() : null);
    orderHistory.setStatusBefore(statusBefore);
    orderHistory.setStatusAfter(statusAfter);
    orderHistory.setNotes(note != null ? note : "Trạng thái thay đổi thành công");
    orderHistory.setCreatedAt(LocalDateTime.now());
    return orderHistory;
  }

  private String createCancellationNote(Order order, String reason, Integer staffId) {
    String sanitizedReason =
        reason != null && !reason.trim().isEmpty() ? reason.trim() : "Không có lý do cụ thể";
    if (sanitizedReason.length() > 255) {
      sanitizedReason = sanitizedReason.substring(0, 255);
    }

    String cancellationNote;
    if (staffId != null) {
      cancellationNote = String.format("Nhân viên hủy đơn, lý do: %s", sanitizedReason);
    } else {
      cancellationNote = "Khách hủy đơn, lý do: " + sanitizedReason;
    }

    return cancellationNote;
  }

  private boolean isOrderCancellable(Order order, boolean isStaff) {
    Status currentStatus = order.getStatus();
    return currentStatus == Status.PENDING_CONFIRMATION;
  }

  public String generateTrackingNumber() {
    String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String randomSuffix = String.format("%06d", (int) (Math.random() * 1000000));
    return "NIKON" + datePrefix + randomSuffix;
  }

  private void sendOrderStatusEmail(Order order, Status status, String reason) {
    try {
      BigDecimal orderTotal =
          order
              .getTotalAmount()
              .subtract(order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO)
              .add(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO);

      switch (status) {
        case PENDING_CONFIRMATION:
          emailService.sendOrderPendingConfirmationEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              orderTotal);
          break;
        case PENDING_PAYMENT:
          emailService.sendOrderPendingPaymentEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              orderTotal,
              "");
          break;
        case CONFIRMED:
          emailService.sendOrderConfirmedEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              orderTotal);
          break;
        case PREPARING:
          emailService.sendOrderPreparingEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              orderTotal);
          break;
        case SHIPPING:
          emailService.sendOrderShippingEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              orderTotal);
          break;
        case COMPLETED:
          emailService.sendOrderCompletedEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              orderTotal);
          break;
        case CANCELLED:
          emailService.sendOrderCancelledEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              reason);
          break;
        case FAILED_DELIVERY:
          emailService.sendOrderFailedDeliveryEmail(
              order.getRecipientEmail(),
              order.getRecipientName(),
              order.getTrackingNumber(),
              reason);
          break;
        default:
          break;
      }
    } catch (Exception e) {
      log.error("Error sending order status email: {}", e.getMessage());
    }
  }

  @Override
  @Transactional
  public void cleanupOldPendingOrders() {
    LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
    List<Order> oldPendingOrders =
        orderRepository.findOldPendingOrders(Status.PENDING_PAYMENT, "IN_STORE", cutoffTime);

    for (Order order : oldPendingOrders) {
      try {
        List<OrderDetail> orderDetails =
            orderDetailRepository.findByOrderIdWithDetails(order.getId());
        List<ProductDetail> productsToUpdate = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetails) {
          ProductDetail productDetail = orderDetail.getProductDetail();
          if (productDetail.getReservedStock() != null
              && productDetail.getReservedStock() >= orderDetail.getQuantity()) {
            productDetail.setReservedStock(
                productDetail.getReservedStock() - orderDetail.getQuantity());
            productsToUpdate.add(productDetail);
          }
        }

        productDetailRepository.saveAll(productsToUpdate);

        orderDetailRepository.deleteAll(orderDetails);
        List<OrderHistory> histories = orderHistoryRepository.findByOrderId(order.getId());
        orderHistoryRepository.deleteAll(histories);

        orderRepository.delete(order);

        log.info("Cleaned up old pending order: {}", order.getTrackingNumber());
      } catch (Exception e) {
        log.error("Error cleaning up order {}: {}", order.getId(), e.getMessage());
      }
    }

    if (!oldPendingOrders.isEmpty()) {
      log.info("Cleaned up {} old pending IN_STORE orders", oldPendingOrders.size());
    }
  }

  @Override
  @Transactional
  public void autoCancelUnpaidOrders() {
    LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
    List<Order> unpaidOrders =
        orderRepository.findUnpaidVnpayOrdersBefore(Status.PENDING_PAYMENT, "VNPAY", threshold);

    for (Order order : unpaidOrders) {
      Status statusBefore = order.getStatus();
      order.setStatus(Status.CANCELLED);
      order.setNote(
          (order.getNote() != null ? order.getNote() + " - " : "") + "Hủy do quá hạn thanh toán");
      order.setUpdatedAt(LocalDateTime.now());

      List<OrderDetail> orderDetails =
          orderDetailRepository.findByOrderIdWithDetails(order.getId());
      for (OrderDetail orderDetail : orderDetails) {
        ProductDetail productDetail = orderDetail.getProductDetail();
        if (productDetail.getReservedStock() != null
            && productDetail.getReservedStock() >= orderDetail.getQuantity()) {
          productDetail.setReservedStock(
              productDetail.getReservedStock() - orderDetail.getQuantity());
          productDetailRepository.save(productDetail);
        }
      }

      OrderHistory orderHistory =
          createOrderHistory(
              order, null, null, statusBefore, Status.CANCELLED, "Hủy do quá hạn thanh toán");
      orderHistoryRepository.save(orderHistory);

      try {
        emailService.sendOrderCancelledEmail(
            order.getRecipientEmail(),
            order.getRecipientName(),
            order.getTrackingNumber(),
            "Hủy do quá hạn thanh toán");
      } catch (Exception e) {
        log.error("Error sending cancellation email: {}", e.getMessage());
      }

      orderRepository.save(order);
    }
  }
}
