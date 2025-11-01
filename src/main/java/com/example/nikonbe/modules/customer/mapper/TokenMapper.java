package com.example.nikonbe.modules.customer.mapper;

import com.example.nikonbe.modules.customer.entity.CustomerToken;
import com.example.nikonbe.security.dto.response.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TokenMapper {

  TokenResponse toDto(CustomerToken token);
}
