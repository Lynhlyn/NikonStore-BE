package com.example.nikonbe.modules.content_tag.mapper;

import com.example.nikonbe.modules.content_tag.dto.request.ContentTagCreateDTO;
import com.example.nikonbe.modules.content_tag.dto.request.ContentTagUpdateDTO;
import com.example.nikonbe.modules.content_tag.dto.response.ContentTagResponseDTO;
import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContentTagMapper {
  @Mapping(target = "id", ignore = true)
  ContentTag toEntity(ContentTagCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(ContentTagUpdateDTO dto, @MappingTarget ContentTag entity);

  ContentTagResponseDTO toDto(ContentTag entity);

  List<ContentTagResponseDTO> toDtoList(List<ContentTag> entities);
}
