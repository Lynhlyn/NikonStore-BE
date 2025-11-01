package com.example.nikonbe.modules.cart_detail.mapper;

import com.example.nikonbe.modules.cart_detail.dto.response.CartItemResponse;
import com.example.nikonbe.modules.cart_detail.entity.CartDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartDetailMapper {
  @Mapping(target = "cartDetailId", source = "id")
  @Mapping(target = "productDetailId", source = "productDetail.id")
  @Mapping(target = "productName", source = "productDetail.product.name")
  @Mapping(target = "sku", source = "productDetail.sku")
  @Mapping(target = "color", source = "productDetail.color.name")
  @Mapping(target = "capacity", source = "productDetail.capacity.name")
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "stock", source = "productDetail.stock")
  CartItemResponse toCartItemResponse(CartDetail cartDetail);
}
