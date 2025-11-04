package com.example.nikonbe.modules.product_tag.mapper;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO;
import com.example.nikonbe.modules.product_tag.entity.ProductTag;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductTagMapper {

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "tagId", source = "tag.id")
  @Mapping(target = "tagName", source = "tag.name")
  @Mapping(target = "tagSlug", source = "tag.slug")
  @Mapping(target = "tagDescription", source = "tag.description")
  @Mapping(target = "tagStatus", source = "tag.status", qualifiedByName = "mapStatusToInteger")
  ProductTagResponseDTO toDto(ProductTag entity);

  List<ProductTagResponseDTO> toDtoList(List<ProductTag> entities);

  @Named("mapStatusToInteger")
  default Integer mapStatusToInteger(Status status) {
    return status != null ? status.getValue() : null;
  }
}
