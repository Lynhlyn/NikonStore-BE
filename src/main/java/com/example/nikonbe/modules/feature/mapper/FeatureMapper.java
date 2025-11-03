package com.example.nikonbe.modules.feature.mapper;

import com.example.nikonbe.modules.feature.dto.request.FeatureCreateDTO;
import com.example.nikonbe.modules.feature.dto.request.FeatureUpdateDTO;
import com.example.nikonbe.modules.feature.dto.response.FeatureResponseDTO;
import com.example.nikonbe.modules.feature.entity.Feature;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeatureMapper {

  @Mapping(target = "id", ignore = true)
  Feature toEntity(FeatureCreateDTO dto);

  @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
  @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
  FeatureResponseDTO toDto(Feature entity);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(FeatureUpdateDTO dto, @MappingTarget Feature entity);

  List<FeatureResponseDTO> toDtoList(List<Feature> entities);
}
