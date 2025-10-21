package com.example.nikonbe.modules.attributes.capacity.mapper;

import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityCreateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityUpdateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.capacity.entity.Capacity;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CapacityMapper {
  @Mapping(target = "id", ignore = true)
  Capacity toEntity(CapacityCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(CapacityUpdateDTO dto, @MappingTarget Capacity entity);

  CapacityResponseDTO toDto(Capacity entity);

  List<CapacityResponseDTO> toDtoList(List<Capacity> entities);
}
