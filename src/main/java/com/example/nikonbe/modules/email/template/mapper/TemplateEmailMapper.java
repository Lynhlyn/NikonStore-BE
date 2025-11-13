package com.example.nikonbe.modules.email.template.mapper;

import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailCreateDTO;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailUpdateDTO;
import com.example.nikonbe.modules.email.template.dto.response.TemplateEmailResponseDTO;
import com.example.nikonbe.modules.email.template.entity.TemplateEmail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TemplateEmailMapper {

  @Mapping(target = "id", ignore = true)
  TemplateEmail toEntity(TemplateEmailCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(TemplateEmailUpdateDTO dto, @MappingTarget TemplateEmail entity);

  TemplateEmailResponseDTO toDto(TemplateEmail entity);

  List<TemplateEmailResponseDTO> toDtoList(List<TemplateEmail> entities);
}
