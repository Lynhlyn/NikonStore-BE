package com.example.nikonbe.modules.product_image.mapper;

import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageCreateDTO;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageUpdateDTO;
import com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO;
import com.example.nikonbe.modules.product_image.entity.ProductImage;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", expression = "java(fromProductId(dto.getProductId()))")
  ProductImage toEntity(ProductImageCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "product", ignore = true)
  void updateEntityFromDto(ProductImageUpdateDTO dto, @MappingTarget ProductImage entity);

  @Mapping(target = "productId", source = "product.id")
  ProductImageResponseDTO toDto(ProductImage entity);

  List<ProductImageResponseDTO> toDtoList(List<ProductImage> entities);

  default Product fromProductId(Integer id) {
    if (id == null) return null;
    Product product = new Product();
    product.setId(id);
    return product;
  }
}
