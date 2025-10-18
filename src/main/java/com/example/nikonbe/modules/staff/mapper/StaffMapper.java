package com.example.nikonbe.modules.staff.mapper;

import com.example.nikonbe.modules.staff.dto.request.StaffCreateDTO;
import com.example.nikonbe.modules.staff.dto.request.StaffUpdateDTO;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import com.example.nikonbe.modules.staff.entity.Staff;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StaffMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "role", ignore = true)
  Staff toEntity(StaffCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "role", ignore = true)
  void updateEntityFromDto(StaffUpdateDTO dto, @MappingTarget Staff entity);

  StaffResponseDTO toDto(Staff entity);

  List<StaffResponseDTO> toDtoList(List<Staff> entities);
}
