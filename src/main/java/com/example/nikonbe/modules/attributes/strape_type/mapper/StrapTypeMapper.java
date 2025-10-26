package com.example.nikonbe.modules.attributes.strape_type.mapper;

import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeCreateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeUpdateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.entity.StrapType;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StrapTypeMapper {
  @Mapping(target = "id", ignore = true)
  StrapType toEntity(StrapTypeCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(StrapTypeUpdateDTO dto, @MappingTarget StrapType entity);

  StrapTypeResponseDTO toDto(StrapType entity);

  List<StrapTypeResponseDTO> toDtoList(List<StrapType> entities);
}
