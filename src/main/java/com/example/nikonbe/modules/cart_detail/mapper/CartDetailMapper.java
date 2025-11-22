package com.example.nikonbe.modules.cart_detail.mapper;

import com.example.nikonbe.modules.cart_detail.dto.response.CartItemResponse;
import com.example.nikonbe.modules.cart_detail.entity.CartDetail;
import com.example.nikonbe.modules.color_image.entity.ColorImage;
import com.example.nikonbe.modules.color_image.repository.ColorImageRepository;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CartDetailMapper {
  @Autowired protected ColorImageRepository colorImageRepository;

  @Mapping(target = "cartDetailId", source = "id")
  @Mapping(target = "productDetailId", source = "productDetail.id")
  @Mapping(target = "productName", source = "productDetail.product.name")
  @Mapping(target = "sku", source = "productDetail.sku")
  @Mapping(target = "color", source = "productDetail.color.name")
  @Mapping(target = "capacity", source = "productDetail.capacity.name")
  @Mapping(target = "imageUrl", source = "productDetail", qualifiedByName = "mapImageUrl")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "stock", source = "productDetail.stock")
  public abstract CartItemResponse toCartItemResponse(CartDetail cartDetail);

  @Named("mapImageUrl")
  protected String mapImageUrl(ProductDetail productDetail) {
    if (productDetail == null
        || productDetail.getProduct() == null
        || productDetail.getColor() == null) {
      return null;
    }

    return colorImageRepository
        .findByProductIdAndColorId(
            productDetail.getProduct().getId(), productDetail.getColor().getId())
        .stream()
        .findFirst()
        .map(ColorImage::getImageUrl)
        .orElse(null);
  }
}
