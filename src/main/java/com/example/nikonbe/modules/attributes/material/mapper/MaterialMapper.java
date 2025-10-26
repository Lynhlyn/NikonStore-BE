package com.example.nikonbe.modules.attributes.material.mapper;

import com.example.nikonbe.modules.attributes.material.dto.request.MaterialCreateDTO;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialUpdateDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.material.entity.Material;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MaterialMapper {
  @Mapping(target = "id", ignore = true)
  Material toEntity(MaterialCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(MaterialUpdateDTO dto, @MappingTarget Material entity);

  MaterialResponseDTO toDto(Material entity);

  List<MaterialResponseDTO> toDtoList(List<Material> entities);
}
