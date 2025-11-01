package com.example.nikonbe.modules.cart.mapper;

import com.example.nikonbe.modules.cart.dto.response.CartResponse;
import com.example.nikonbe.modules.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartMapper {
  @Mapping(target = "cartId", source = "id")
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "cookieId", source = "cookieId")
  @Mapping(target = "items", source = "cartDetails")
  CartResponse toCartResponse(Cart cart);
}
