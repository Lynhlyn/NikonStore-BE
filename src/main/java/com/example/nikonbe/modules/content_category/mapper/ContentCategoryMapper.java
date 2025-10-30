package com.example.nikonbe.modules.content_category.mapper;

import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryCreateDTO;
import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryUpdateDTO;
import com.example.nikonbe.modules.content_category.dto.response.ContentCategoryResponseDTO;
import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContentCategoryMapper {
  @Mapping(target = "id", ignore = true)
  ContentCategory toEntity(ContentCategoryCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(ContentCategoryUpdateDTO dto, @MappingTarget ContentCategory entity);

  ContentCategoryResponseDTO toDto(ContentCategory entity);

  List<ContentCategoryResponseDTO> toDtoList(List<ContentCategory> entities);
}
