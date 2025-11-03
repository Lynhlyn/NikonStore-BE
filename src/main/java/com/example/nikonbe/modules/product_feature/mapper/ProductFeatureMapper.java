package com.example.nikonbe.modules.product_feature.mapper;

import com.example.nikonbe.modules.product_feature.dto.response.ProductFeatureResponseDTO;
import com.example.nikonbe.modules.product_feature.entity.ProductFeature;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductFeatureMapper {

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "featureId", source = "feature.id")
  @Mapping(target = "featureName", source = "feature.name")
  @Mapping(target = "featureDescription", source = "feature.description")
  @Mapping(target = "featureGroup", source = "feature.featureGroup")
  ProductFeatureResponseDTO toDto(ProductFeature entity);

  List<ProductFeatureResponseDTO> toDtoList(List<ProductFeature> entities);
}
