package com.example.nikonbe.modules.color_image.mapper;

import com.example.nikonbe.modules.color_image.dto.request.ColorImageCreateDTO;
import com.example.nikonbe.modules.color_image.dto.request.ColorImageUpdateDTO;
import com.example.nikonbe.modules.color_image.dto.response.ColorImageResponseDTO;
import com.example.nikonbe.modules.color_image.entity.ColorImage;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ColorImageMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "color.id", source = "colorId")
  ColorImage toEntity(ColorImageCreateDTO dto);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "colorId", source = "color.id")
  ColorImageResponseDTO toDto(ColorImage entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "color.id", source = "colorId")
  void updateEntityFromDto(ColorImageUpdateDTO dto, @MappingTarget ColorImage entity);

  List<ColorImageResponseDTO> toDtoList(List<ColorImage> entities);
}
