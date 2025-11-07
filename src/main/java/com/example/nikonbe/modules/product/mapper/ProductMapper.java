package com.example.nikonbe.modules.product.mapper;

import com.example.nikonbe.modules.attributes.brand.entity.Brand;
import com.example.nikonbe.modules.attributes.brand.mapper.BrandMapper;
import com.example.nikonbe.modules.attributes.category.entity.Category;
import com.example.nikonbe.modules.attributes.category.mapper.CategoryMapper;
import com.example.nikonbe.modules.attributes.material.entity.Material;
import com.example.nikonbe.modules.attributes.material.mapper.MaterialMapper;
import com.example.nikonbe.modules.attributes.strape_type.entity.StrapType;
import com.example.nikonbe.modules.attributes.strape_type.mapper.StrapTypeMapper;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product_image.mapper.ProductImageMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {
      ProductImageMapper.class,
      StrapTypeMapper.class,
      BrandMapper.class,
      CategoryMapper.class,
      MaterialMapper.class
    })
public interface ProductMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "strapType", expression = "java(fromStrapTypeId(dto.getStrapTypeId()))")
  @Mapping(target = "brand", expression = "java(fromBrandId(dto.getBrandId()))")
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "material", expression = "java(fromMaterialId(dto.getMaterialId()))")
  Product toEntity(ProductCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "strapType", expression = "java(fromStrapTypeId(dto.getStrapTypeId()))")
  @Mapping(target = "brand", expression = "java(fromBrandId(dto.getBrandId()))")
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "material", expression = "java(fromMaterialId(dto.getMaterialId()))")
  void updateEntityFromDto(ProductUpdateDTO dto, @MappingTarget Product entity);

  @Mapping(target = "images", ignore = true)
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
