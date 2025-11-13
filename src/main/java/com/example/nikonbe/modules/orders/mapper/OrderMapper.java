package com.example.nikonbe.modules.orders.mapper;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.orders.dto.response.GetOrderDetailResponse;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderAllResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderResponse;
import com.example.nikonbe.modules.orders.entity.Order;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {
  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "trackingNumber", source = "trackingNumber")
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "orderStatus", source = "status", qualifiedByName = "statusToInteger")
  @Mapping(target = "orderDate", source = "createdAt", qualifiedByName = "localDateTimeToString")
  @Mapping(target = "customerName", source = "recipientName")
  @Mapping(target = "customerEmail", source = "recipientEmail")
  @Mapping(target = "customerPhone", source = "recipientPhone")
  @Mapping(target = "shippingAddress", source = "shippingAddress")
  @Mapping(target = "totalAmount", source = "totalAmount")
  @Mapping(target = "discount", source = "discount")
  @Mapping(target = "shippingFee", source = "shippingFee")
  @Mapping(target = "paymentMethod", source = "paymentMethod")
  @Mapping(target = "orderType", source = "orderType")
  @Mapping(target = "paymentStatus", source = "paymentStatus")
  @Mapping(target = "note", source = "note")
  @Mapping(target = "orderDetails", source = "orderDetails")
  GetOrderDetailResponse toGetOrderDetailResponse(Order order);

  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "trackingNumber", source = "trackingNumber")
  @Mapping(target = "orderStatus", source = "status", qualifiedByName = "statusToInteger")
  @Mapping(target = "orderDate", source = "createdAt")
  @Mapping(target = "totalAmount", source = "totalAmount")
  @Mapping(target = "paymentMethod", source = "paymentMethod")
  @Mapping(target = "note", source = "note")
  @Mapping(target = "discount", source = "discount")
  @Mapping(target = "shippingFee", source = "shippingFee")
  @Mapping(target = "remainingPaymentTime", expression = "java(mapRemainingPaymentTime(order))")
  OrderResponse toOrderResponse(Order order);

  @Mapping(target = "trackingNumber", source = "trackingNumber")
  @Mapping(target = "orderid", source = "id")
  @Mapping(target = "orderStatus", source = "status", qualifiedByName = "statusToInteger")
  @Mapping(target = "orderDate", source = "createdAt")
  @Mapping(target = "totalAmount", source = "totalAmount")
  @Mapping(target = "discount", source = "discount")
  @Mapping(target = "shippingFee", source = "shippingFee")
  @Mapping(target = "ordertype", source = "orderType")
  @Mapping(target = "paymentMethod", source = "paymentMethod")
  @Mapping(target = "paymentStatus", source = "paymentStatus")
  @Mapping(target = "shippingAddress", source = "shippingAddress")
  @Mapping(target = "notes", source = "note")
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "customerName", source = "recipientName")
  @Mapping(target = "customerPhone", source = "recipientPhone")
  @Mapping(target = "customerEmail", source = "recipientEmail")
  @Mapping(target = "recipientPhone", source = "recipientPhone")
  @Mapping(target = "recipientEmail", source = "recipientEmail")
  @Mapping(target = "voucherCode", expression = "java(getVoucherCode(order))")
  @Mapping(target = "staffName", source = "staff.fullName")
  OrderAllResponse toOrderAllResponse(Order order);

  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "orderStatus", source = "status", qualifiedByName = "statusToInteger")
  @Mapping(target = "orderDate", source = "createdAt", qualifiedByName = "localDateTimeToString")
  @Mapping(target = "totalAmount", source = "totalAmount")
  @Mapping(target = "discount", source = "discount")
  @Mapping(target = "shippingFee", source = "shippingFee")
  @Mapping(target = "paymentMethod", source = "paymentMethod")
  @Mapping(target = "paymentStatus", source = "paymentStatus")
  @Mapping(target = "shippingAddress", source = "shippingAddress")
  @Mapping(target = "note", source = "note")
  @Mapping(target = "trackingNumber", source = "trackingNumber")
  @Mapping(target = "customerName", expression = "java(getCustomerName(order))")
  @Mapping(target = "customerEmail", expression = "java(getCustomerEmail(order))")
  @Mapping(target = "customerPhone", expression = "java(getCustomerPhone(order))")
  @Mapping(target = "orderDetails", source = "orderDetails")
  @Mapping(target = "orderType", source = "orderType")
  @Mapping(target = "staffId", source = "staff.id")
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "finalAmount", expression = "java(calculateFinalAmount(order))")
  ListOrderResponse toCreateOrderResponse(Order order);

  @Named("statusToInteger")
  default Integer map(Status status) {
    return status != null ? status.getValue() : null;
  }

  @Named("localDateTimeToString")
  default String localDateTimeToString(java.time.LocalDateTime dateTime) {
    if (dateTime == null) {
      return null;
    }
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  default Long mapRemainingPaymentTime(Order order) {
    if (order == null) return null;
    if (order.getStatus() != null
        && order.getStatus() == Status.PENDING_PAYMENT
        && order.getCreatedAt() != null) {
      java.time.LocalDateTime now = java.time.LocalDateTime.now();
      java.time.LocalDateTime expired = order.getCreatedAt().plusMinutes(30);
      long seconds = java.time.Duration.between(now, expired).getSeconds();
      return seconds > 0 ? seconds : 0L;
    }
    return null;
  }

  default String getVoucherCode(Order order) {
    if (order == null || order.getVoucher() == null) {
      return null;
    }
    return order.getVoucher().getCode();
  }

  default String getCustomerName(Order order) {
    if (order == null) {
      return null;
    }
    if (order.getCustomer() != null) {
      return order.getCustomer().getFullName();
    }
    return order.getRecipientName();
  }

  default String getCustomerEmail(Order order) {
    if (order == null) {
      return null;
    }
    if (order.getCustomer() != null) {
      return order.getCustomer().getEmail();
    }
    return order.getRecipientEmail();
  }

  default String getCustomerPhone(Order order) {
    if (order == null) {
      return null;
    }
    if (order.getCustomer() != null) {
      return order.getCustomer().getPhoneNumber();
    }
    return order.getRecipientPhone();
  }

  default java.math.BigDecimal calculateFinalAmount(Order order) {
    if (order == null) {
      return null;
    }
    java.math.BigDecimal total =
        order.getTotalAmount() != null ? order.getTotalAmount() : java.math.BigDecimal.ZERO;
    java.math.BigDecimal discount =
        order.getDiscount() != null ? order.getDiscount() : java.math.BigDecimal.ZERO;
    java.math.BigDecimal shippingFee =
        order.getShippingFee() != null ? order.getShippingFee() : java.math.BigDecimal.ZERO;
    return total.subtract(discount).add(shippingFee);
  }
}
