package com.example.nikonbe.modules.attributes.color.mapper;

import com.example.nikonbe.modules.attributes.color.dto.request.ColorCreateDTO;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorUpdateDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import com.example.nikonbe.modules.attributes.color.entity.Color;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ColorMapper {
  @Mapping(target = "id", ignore = true)
  Color toEntity(ColorCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(ColorUpdateDTO dto, @MappingTarget Color entity);

  ColorResponseDTO toDto(Color entity);

  List<ColorResponseDTO> toDtoList(List<Color> entities);
}
