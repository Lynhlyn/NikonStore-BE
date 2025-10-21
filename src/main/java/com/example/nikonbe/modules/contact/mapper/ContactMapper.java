package com.example.nikonbe.modules.contact.mapper;

import com.example.nikonbe.modules.contact.dto.request.ContactCreateDTO;
import com.example.nikonbe.modules.contact.dto.request.ContactUpdateDTO;
import com.example.nikonbe.modules.contact.dto.response.ContactResponseDTO;
import com.example.nikonbe.modules.contact.entity.Contact;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContactMapper {
  @Mapping(target = "id", ignore = true)
  Contact toEntity(ContactCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(ContactUpdateDTO dto, @MappingTarget Contact entity);

  ContactResponseDTO toDto(Contact entity);

  List<ContactResponseDTO> toDtoList(List<Contact> entities);
}
