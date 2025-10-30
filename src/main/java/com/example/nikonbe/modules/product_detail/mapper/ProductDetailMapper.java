package com.example.nikonbe.modules.product_detail.mapper;

import com.example.nikonbe.modules.attributes.capacity.entity.Capacity;
import com.example.nikonbe.modules.attributes.color.entity.Color;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailCreateDTO;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailUpdateDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", expression = "java(fromProductId(dto.getProductId()))")
  @Mapping(target = "color", expression = "java(fromColorId(dto.getColorId()))")
  @Mapping(target = "capacity", expression = "java(fromCapacityId(dto.getCapacityId()))")
  ProductDetail toEntity(ProductDetailCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "product", expression = "java(fromProductId(dto.getProductId()))")
  @Mapping(target = "color", expression = "java(fromColorId(dto.getColorId()))")
  @Mapping(target = "capacity", expression = "java(fromCapacityId(dto.getCapacityId()))")
  void updateEntityFromDto(ProductDetailUpdateDTO dto, @MappingTarget ProductDetail entity);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "colorId", source = "color.id")
  @Mapping(target = "colorName", source = "color.name")
  @Mapping(target = "capacityId", source = "capacity.id")
  @Mapping(target = "capacityName", source = "capacity.name")
  ProductDetailResponseDTO toDto(ProductDetail entity);

  default Product fromProductId(Integer id) {
    if (id == null) return null;
    Product e = new Product();
    e.setId(id);
    return e;
  }

  default Color fromColorId(Integer id) {
    if (id == null) return null;
    Color e = new Color();
    e.setId(id);
    return e;
  }

  default Capacity fromCapacityId(Integer id) {
    if (id == null) return null;
    Capacity e = new Capacity();
    e.setId(id);
    return e;
  }
}
