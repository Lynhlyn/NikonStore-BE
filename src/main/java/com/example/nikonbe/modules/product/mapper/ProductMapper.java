package com.example.nikonbe.modules.product.mapper;

import com.example.nikonbe.modules.attributes.brand.entity.Brand;
import com.example.nikonbe.modules.attributes.category.entity.Category;
import com.example.nikonbe.modules.attributes.material.entity.Material;
import com.example.nikonbe.modules.attributes.strape_type.entity.StrapType;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "strapType", expression = "java(fromStrapTypeId(dto.getStrapTypeId()))")
  @Mapping(target = "brand", expression = "java(fromBrandId(dto.getBrandId()))")
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "material", expression = "java(fromMaterialId(dto.getMaterialId()))")
  Product toEntity(ProductCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "strapType", expression = "java(fromStrapTypeId(dto.getStrapTypeId()))")
  @Mapping(target = "brand", expression = "java(fromBrandId(dto.getBrandId()))")
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "material", expression = "java(fromMaterialId(dto.getMaterialId()))")
  void updateEntityFromDto(ProductUpdateDTO dto, @MappingTarget Product entity);

  @Mapping(target = "strapTypeId", source = "strapType.id")
  @Mapping(target = "strapTypeName", source = "strapType.name")
  @Mapping(target = "brandId", source = "brand.id")
  @Mapping(target = "brandName", source = "brand.name")
  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  @Mapping(target = "materialId", source = "material.id")
  @Mapping(target = "materialName", source = "material.name")
  ProductResponseDTO toDto(Product entity);

  default StrapType fromStrapTypeId(Integer id) {
    if (id == null) return null;
    StrapType e = new StrapType();
    e.setId(id);
    return e;
  }

  default Brand fromBrandId(Integer id) {
    if (id == null) return null;
    Brand e = new Brand();
    e.setId(id);
    return e;
  }

  default Category fromCategoryId(Integer id) {
    if (id == null) return null;
    Category e = new Category();
    e.setId(id);
    return e;
  }

  default Material fromMaterialId(Integer id) {
    if (id == null) return null;
    Material e = new Material();
    e.setId(id);
    return e;
  }
}
