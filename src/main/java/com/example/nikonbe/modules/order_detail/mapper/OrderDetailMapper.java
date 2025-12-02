package com.example.nikonbe.modules.order_detail.mapper;

import com.example.nikonbe.modules.color_image.repository.ColorImageRepository;
import com.example.nikonbe.modules.order_detail.dto.response.OrderDetailReponse;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrderDetailMapper {
  @Autowired ColorImageRepository colorImageRepository;

  @Mapping(target = "orderDetailId", source = "id")
  @Mapping(target = "productId", source = "productDetail.product.id")
  @Mapping(target = "sku", source = "productDetail.sku")
  @Mapping(target = "productName", source = "productDetail.product.name")
  @Mapping(target = "brandName", source = "productDetail.product.brand.name")
  @Mapping(target = "categoryName", source = "productDetail.product.category.name")
  @Mapping(target = "colorName", source = "productDetail.color.name")
  @Mapping(target = "capacityName", source = "productDetail.capacity.name")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "dimensions", source = "productDetail.product.dimensions")
  @Mapping(target = "compartment", source = "productDetail.product.compartment")
  @Mapping(target = "strapTypeName", source = "productDetail.product.strapType.name")
  @Mapping(target = "imageUrl", source = "productDetail", qualifiedByName = "mapImageUrl")
  public abstract OrderDetailReponse toOrderProductResponse(OrderDetail orderDetail);

  @Named("mapImageUrl")
  public String mapImageUrl(ProductDetail productDetail) {
    if (productDetail == null
        || productDetail.getProduct() == null
        || productDetail.getColor() == null) {
      return null;
    }
    return colorImageRepository
        .findByProductIdAndColorId(
            productDetail.getProduct().getId(), productDetail.getColor().getId())
        .map(com.example.nikonbe.modules.color_image.entity.ColorImage::getImageUrl)
        .orElse(null);
  }
}
