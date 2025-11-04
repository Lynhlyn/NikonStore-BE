package com.example.nikonbe.modules.tag.mapper;

import com.example.nikonbe.modules.tag.dto.request.TagCreateDTO;
import com.example.nikonbe.modules.tag.dto.request.TagUpdateDTO;
import com.example.nikonbe.modules.tag.dto.response.TagResponseDTO;
import com.example.nikonbe.modules.tag.entity.Tag;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TagMapper {

  @Mapping(target = "id", ignore = true)
  Tag toEntity(TagCreateDTO dto);

  @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
  @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
  TagResponseDTO toDto(Tag entity);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(TagUpdateDTO dto, @MappingTarget Tag entity);

  List<TagResponseDTO> toDtoList(List<Tag> entities);
}
