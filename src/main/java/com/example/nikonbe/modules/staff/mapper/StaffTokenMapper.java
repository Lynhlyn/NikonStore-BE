package com.example.nikonbe.modules.staff.mapper;

import com.example.nikonbe.modules.staff.entity.StaffToken;
import com.example.nikonbe.security.dto.response.TokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StaffTokenMapper {

  TokenResponse toDto(StaffToken entity);
}
