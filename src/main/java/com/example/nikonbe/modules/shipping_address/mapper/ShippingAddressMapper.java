package com.example.nikonbe.modules.shipping_address.mapper;

import com.example.nikonbe.modules.shipping_address.dto.request.CreateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.request.UpdateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.response.ShippingAddressResponseDto;
import com.example.nikonbe.modules.shipping_address.entity.ShippingAddress;
import java.util.List;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShippingAddressMapper {

  /** Chuyển đổi từ Entity sang response dto */
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "fullAddress", expression = "java(buildFullAddress(shippingAddress))")
  ShippingAddressResponseDto toDto(ShippingAddress shippingAddress);

  /** Xây dựng địa chỉ đầy đủ */
  default String buildFullAddress(ShippingAddress address) {
    if (address == null) return null;

    StringBuilder sb = new StringBuilder();
    if (address.getDetailedAddress() != null) {
      sb.append(address.getDetailedAddress());
    }
    if (address.getWard() != null) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(address.getWard());
    }
    if (address.getDistrict() != null) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(address.getDistrict());
    }
    if (address.getProvince() != null) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(address.getProvince());
    }
    return sb.toString();
  }

  /** Chuyển đổi danh sách Entity sang danh sách response dto */
  List<ShippingAddressResponseDto> toDtoList(List<ShippingAddress> entities);

  /** Chuyển đổi từ create dto sang entity */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "isDefault", source = "isDefault")
  ShippingAddress toEntity(CreateShippingAddressDTO dto);

  /** Cập nhật entity từ update dto */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customer", ignore = true)
  void updateEntityFromDto(UpdateShippingAddressDTO dto, @MappingTarget ShippingAddress entity);
}
