package com.example.nikonbe.modules.customer.mapper;

import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.entity.Customer;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "providerId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "stringToLocalDate")
  Customer toEntity(CustomerCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "providerId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "stringToLocalDate")
  void updateEntityFromDto(CustomerUpdateDTO dto, @MappingTarget Customer entity);

  @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "localDateToString")
  CustomerResponseDTO toDto(Customer entity);

  List<CustomerResponseDTO> toDtoList(List<Customer> entities);

  @Named("stringToLocalDate")
  default LocalDate stringToLocalDate(String dateString) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(dateString);
    } catch (Exception e) {
      return null;
    }
  }

  @Named("localDateToString")
  default String localDateToString(LocalDate localDate) {
    return localDate != null ? localDate.toString() : null;
  }
}
