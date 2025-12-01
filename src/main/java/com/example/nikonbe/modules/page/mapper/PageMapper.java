package com.example.nikonbe.modules.page.mapper;

import com.example.nikonbe.modules.page.dto.request.PageCreateDto;
import com.example.nikonbe.modules.page.dto.request.PageUpdateDto;
import com.example.nikonbe.modules.page.dto.response.PageAdminDto;
import com.example.nikonbe.modules.page.dto.response.PageDto;
import com.example.nikonbe.modules.page.entity.Page;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PageMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Page toEntity(PageCreateDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(PageUpdateDto dto, @MappingTarget Page entity);

  PageDto toClientDto(Page entity);

  PageAdminDto toAdminDto(Page entity);
}
