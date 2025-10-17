package com.example.nikonbe.modules.attributes.category.mapper;

import com.example.nikonbe.modules.attributes.category.dto.request.CategoryCreateDTO;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryUpdateDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.category.entity.Category;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "parent", ignore = true)
  Category toEntity(CategoryCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "parent", ignore = true)
  void updateEntityFromDto(CategoryUpdateDTO dto, @MappingTarget Category entity);

  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "parentName", source = "parent.name")
  CategoryResponseDTO toDto(Category entity);

  List<CategoryResponseDTO> toDtoList(List<Category> entities);
}
