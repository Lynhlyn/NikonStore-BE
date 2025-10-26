package com.example.nikonbe.modules.attributes.strape_type.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeCreateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeUpdateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.entity.StrapType;
import com.example.nikonbe.modules.attributes.strape_type.mapper.StrapTypeMapper;
import com.example.nikonbe.modules.attributes.strape_type.repository.StrapTypeRepository;
import com.example.nikonbe.modules.attributes.strape_type.service.interF.StrapTypeService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrapTypeServiceImpl implements StrapTypeService {

  private final StrapTypeRepository strapTypeRepository;
  private final StrapTypeMapper strapTypeMapper;

  @Transactional
  @Override
  public StrapTypeResponseDTO create(StrapTypeCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (strapTypeRepository.existsByName(dto.getName())) {
      errors.put("name", "Strap type name already exists");
    }
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Strap type name is required");
    }
    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    StrapType strapType = strapTypeMapper.toEntity(dto);
    StrapType saved = strapTypeRepository.save(strapType);
    return strapTypeMapper.toDto(saved);
  }

  @Transactional
  @Override
  public StrapTypeResponseDTO update(Integer id, StrapTypeUpdateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    StrapType strapType =
        strapTypeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("StrapType", "id", id));

    if (strapTypeRepository.existsByNameAndIdNot(dto.getName(), id)) {
      errors.put("name", "Strap type name already exists");
    }
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Strap type name is required");
    }
    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    strapTypeMapper.updateEntityFromDto(dto, strapType);
    StrapType updated = strapTypeRepository.save(strapType);

    return strapTypeMapper.toDto(updated);
  }

  @Transactional(readOnly = true)
  @Override
  public StrapTypeResponseDTO getById(Integer id) {
    StrapType strapType =
        strapTypeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("StrapType", "id", id));
    return strapTypeMapper.toDto(strapType);
  }

  @Transactional(readOnly = true)
  @Override
  public List<StrapTypeResponseDTO> getAll() {
    List<StrapType> list = strapTypeRepository.findAll();
    return strapTypeMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public List<StrapTypeResponseDTO> getAllByStatus(Status status) {
    List<StrapType> list = strapTypeRepository.findByStatus(status);
    return strapTypeMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<StrapTypeResponseDTO> getAllPaginated(Pageable pageable) {
    Page<StrapType> page = strapTypeRepository.findAll(pageable);
    return page.map(strapTypeMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<StrapTypeResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    Page<StrapType> page = strapTypeRepository.findByStatus(status, pageable);
    return page.map(strapTypeMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    StrapType strapType =
        strapTypeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("StrapType", "id", id));
    strapType.setStatus(Status.DELETED);
    strapTypeRepository.save(strapType);
  }

  @Override
  public boolean existsByName(String name) {
    return strapTypeRepository.existsByName(name);
  }

  @Transactional(readOnly = true)
  @Override
  public List<StrapTypeResponseDTO> search(String keyword) {
    List<StrapType> list = strapTypeRepository.findByNameContainingIgnoreCase(keyword);
    return strapTypeMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<StrapTypeResponseDTO> searchPaginated(String keyword, Pageable pageable) {
    Page<StrapType> page = strapTypeRepository.findByNameContainingIgnoreCase(keyword, pageable);
    return page.map(strapTypeMapper::toDto);
  }
}
