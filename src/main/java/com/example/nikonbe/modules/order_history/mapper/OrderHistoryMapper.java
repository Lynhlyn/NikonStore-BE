package com.example.nikonbe.modules.order_history.mapper;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.modules.order_history.dto.response.OrderHistoryResponse;
import com.example.nikonbe.modules.order_history.entity.OrderHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderHistoryMapper {

  @Mapping(source = "order.id", target = "orderId")
  @Mapping(source = "order.trackingNumber", target = "trackingNumber")
  @Mapping(source = "order.recipientName", target = "customerName")
  @Mapping(
      source = "changeByType",
      target = "changeByType",
      qualifiedByName = "changeByTypeToString")
  @Mapping(source = "order.orderType", target = "orderType")
  @Mapping(source = ".", target = "changeByName", qualifiedByName = "changeByNameToString")
  @Mapping(source = "statusBefore", target = "statusBefore", qualifiedByName = "statusToInteger")
  @Mapping(source = "statusAfter", target = "statusAfter", qualifiedByName = "statusToInteger")
  @Mapping(source = "notes", target = "notes")
  @Mapping(source = "createdAt", target = "createdAt")
  OrderHistoryResponse toOrderHistoryResponse(OrderHistory orderHistory);

  @Named("changeByTypeToString")
  default String changeByTypeToString(UserRole changeByType) {
    if (changeByType == null) {
      return UserRole.system.name();
    }
    return changeByType.name();
  }

  @Named("changeByNameToString")
  default String changeByNameToString(OrderHistory orderHistory) {
    if (orderHistory == null) {
      return "System";
    }
    UserRole changeByType = orderHistory.getChangeByType();
    if (changeByType == UserRole.customer && orderHistory.getCustomer() != null) {
      return orderHistory.getCustomer().getFullName();
    } else if (changeByType == UserRole.staff && orderHistory.getStaff() != null) {
      return orderHistory.getStaff().getFullName();
    }
    return "System";
  }

  @Named("statusToInteger")
  default Integer map(Status status) {
    return status != null ? status.getValue() : null;
  }
}
