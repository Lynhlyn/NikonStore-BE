package com.example.nikonbe.modules.attributes.brand.mapper;

import com.example.nikonbe.modules.attributes.brand.dto.request.BrandCreateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandUpdateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.brand.entity.Brand;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

  @Mapping(target = "id", ignore = true)
  Brand toEntity(BrandCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromDto(BrandUpdateDTO dto, @MappingTarget Brand entity);

  BrandResponseDTO toDto(Brand entity);

  List<BrandResponseDTO> toDtoList(List<Brand> entities);
}
