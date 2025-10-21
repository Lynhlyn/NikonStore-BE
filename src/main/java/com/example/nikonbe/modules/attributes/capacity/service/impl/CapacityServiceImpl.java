package com.example.nikonbe.modules.attributes.capacity.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityCreateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityUpdateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.capacity.entity.Capacity;
import com.example.nikonbe.modules.attributes.capacity.mapper.CapacityMapper;
import com.example.nikonbe.modules.attributes.capacity.repository.CapacityRepository;
import com.example.nikonbe.modules.attributes.capacity.service.interF.CapacityService;
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
public class CapacityServiceImpl implements CapacityService {

  private final CapacityRepository capacityRepository;
  private final CapacityMapper capacityMapper;

  @Transactional
  @Override
  public CapacityResponseDTO create(CapacityCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();
    if (capacityRepository.existsByName(dto.getName())) {
      errors.put("name", "Capacity name already exists");
    }
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Capacity name is required");
    }
    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
    Capacity capacity = capacityMapper.toEntity(dto);
    Capacity saved = capacityRepository.save(capacity);
    return capacityMapper.toDto(saved);
  }

  @Transactional
  @Override
  public CapacityResponseDTO update(Integer id, CapacityUpdateDTO dto) {
    Map<String, String> errors = new HashMap<>();
    Capacity capacity =
        capacityRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capacity", "id", id));
    if (capacityRepository.existsByNameAndIdNot(dto.getName(), id)) {
      errors.put("name", "Capacity name already exists");
    }
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Capacity name is required");
    }
    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    capacityMapper.updateEntityFromDto(dto, capacity);
    Capacity updated = capacityRepository.save(capacity);

    return capacityMapper.toDto(updated);
  }

  @Transactional(readOnly = true)
  @Override
  public CapacityResponseDTO getById(Integer id) {
    Capacity capacity =
        capacityRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capacity", "id", id));
    return capacityMapper.toDto(capacity);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CapacityResponseDTO> getAll() {
    List<Capacity> list = capacityRepository.findAll();
    return capacityMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CapacityResponseDTO> getAllByStatus(Status status) {
    List<Capacity> list = capacityRepository.findByStatus(status);
    return capacityMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CapacityResponseDTO> getAllPaginated(Pageable pageable) {
    Page<Capacity> page = capacityRepository.findAll(pageable);
    return page.map(capacityMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CapacityResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    Page<Capacity> page = capacityRepository.findByStatus(status, pageable);
    return page.map(capacityMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    Capacity capacity =
        capacityRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capacity", "id", id));
    capacity.setStatus(Status.DELETED);
    capacityRepository.save(capacity);
  }

  @Override
  public boolean existsByName(String name) {
    return capacityRepository.existsByName(name);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CapacityResponseDTO> search(String keyword) {
    List<Capacity> list = capacityRepository.findByNameContainingIgnoreCase(keyword);
    return capacityMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CapacityResponseDTO> searchPaginated(String keyword, Pageable pageable) {
    Page<Capacity> page = capacityRepository.findByNameContainingIgnoreCase(keyword, pageable);
    return page.map(capacityMapper::toDto);
  }
}
