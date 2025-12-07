package com.example.nikonbe.modules.contact.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.contact.dto.request.ContactCreateDTO;
import com.example.nikonbe.modules.contact.dto.request.ContactUpdateDTO;
import com.example.nikonbe.modules.contact.dto.response.ContactResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService {
  ContactResponseDTO create(ContactCreateDTO dto);

  ContactResponseDTO update(Integer id, ContactUpdateDTO dto);

  ContactResponseDTO getById(Integer id);

  ContactResponseDTO getByIdAndMarkAsRead(Integer id);

  List<ContactResponseDTO> getAll();

  Page<ContactResponseDTO> getAllPaginated(Pageable pageable);

  List<ContactResponseDTO> getAllByStatus(Status status);

  Page<ContactResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByPhone(String phone);
}
