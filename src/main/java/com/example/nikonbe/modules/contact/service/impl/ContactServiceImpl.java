package com.example.nikonbe.modules.contact.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.contact.dto.request.ContactCreateDTO;
import com.example.nikonbe.modules.contact.dto.request.ContactUpdateDTO;
import com.example.nikonbe.modules.contact.dto.response.ContactResponseDTO;
import com.example.nikonbe.modules.contact.entity.Contact;
import com.example.nikonbe.modules.contact.mapper.ContactMapper;
import com.example.nikonbe.modules.contact.repository.ContactRepository;
import com.example.nikonbe.modules.contact.service.interF.ContactService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {
  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;

  @Override
  public ContactResponseDTO create(ContactCreateDTO dto) {
    Contact contact = contactMapper.toEntity(dto);
    Contact saved = contactRepository.save(contact);
    return contactMapper.toDto(saved);
  }

  @Override
  public ContactResponseDTO update(Integer id, ContactUpdateDTO dto) {
    Contact contact =
        contactRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
    contactMapper.updateEntityFromDto(dto, contact);
    contact.setUpdatedAt(LocalDateTime.now());
    Contact updated = contactRepository.save(contact);
    return contactMapper.toDto(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public ContactResponseDTO getById(Integer id) {
    Contact contact =
        contactRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
    return contactMapper.toDto(contact);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContactResponseDTO> getAll() {
    return contactMapper.toDtoList(contactRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContactResponseDTO> getAllPaginated(Pageable pageable) {
    return contactRepository.findAll(pageable).map(contactMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContactResponseDTO> getAllByStatus(Status status) {
    return contactMapper.toDtoList(contactRepository.findByStatus(status));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContactResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    return contactRepository.findByStatus(status, pageable).map(contactMapper::toDto);
  }

  @Override
  public void delete(Integer id) {
    Contact contact =
        contactRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
    contact.setStatus(Status.DELETED);
    contactRepository.save(contact);
  }

  @Override
  public boolean existsByPhone(String phone) {
    return contactRepository.existsByPhone(phone);
  }
}
