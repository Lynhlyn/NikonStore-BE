package com.example.nikonbe.modules.banner.mapper;

import com.example.nikonbe.modules.banner.dto.request.BannerCreateDTO;
import com.example.nikonbe.modules.banner.dto.request.BannerUpdateDTO;
import com.example.nikonbe.modules.banner.dto.response.BannerResponseDTO;
import com.example.nikonbe.modules.banner.entity.Banner;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BannerMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Banner toEntity(BannerCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromDto(BannerUpdateDTO dto, @MappingTarget Banner entity);

  BannerResponseDTO toDto(Banner entity);

  List<BannerResponseDTO> toDtoList(List<Banner> entities);
}
