package com.example.nikonbe.modules.attributes.material.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialCreateDTO;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialUpdateDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.material.entity.Material;
import com.example.nikonbe.modules.attributes.material.mapper.MaterialMapper;
import com.example.nikonbe.modules.attributes.material.repository.MaterialRepository;
import com.example.nikonbe.modules.attributes.material.service.interF.MaterialService;
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
public class MaterialServiceImpl implements MaterialService {

  private final MaterialRepository materialRepository;
  private final MaterialMapper materialMapper;

  @Transactional
  @Override
  public MaterialResponseDTO create(MaterialCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();
    if (materialRepository.existsByName(dto.getName())) {
      errors.put("name", "Material name already exists");
    }
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Material name is required");
    }
    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
    Material material = materialMapper.toEntity(dto);
    Material saved = materialRepository.save(material);
    return materialMapper.toDto(saved);
  }

  @Transactional
  @Override
  public MaterialResponseDTO update(Integer id, MaterialUpdateDTO dto) {
    Map<String, String> errors = new HashMap<>();
    Material material =
        materialRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
    if (materialRepository.existsByNameAndIdNot(dto.getName(), id)) {
      errors.put("name", "Material name already exists");
    }
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Material name is required");
    }
    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }
    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    materialMapper.updateEntityFromDto(dto, material);
    Material updated = materialRepository.save(material);

    return materialMapper.toDto(updated);
  }

  @Transactional(readOnly = true)
  @Override
  public MaterialResponseDTO getById(Integer id) {
    Material material =
        materialRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
    return materialMapper.toDto(material);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MaterialResponseDTO> getAll() {
    List<Material> list = materialRepository.findAll();
    return materialMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MaterialResponseDTO> getAllByStatus(Status status) {
    List<Material> list = materialRepository.findByStatus(status);
    return materialMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<MaterialResponseDTO> getAllPaginated(Pageable pageable) {
    Page<Material> page = materialRepository.findAll(pageable);
    return page.map(materialMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<MaterialResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    Page<Material> page = materialRepository.findByStatus(status, pageable);
    return page.map(materialMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    Material material =
        materialRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
    material.setStatus(Status.DELETED);
    materialRepository.save(material);
  }

  @Override
  public boolean existsByName(String name) {
    return materialRepository.existsByName(name);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MaterialResponseDTO> search(String keyword) {
    List<Material> list = materialRepository.findByNameContainingIgnoreCase(keyword);
    return materialMapper.toDtoList(list);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<MaterialResponseDTO> searchPaginated(String keyword, Pageable pageable) {
    Page<Material> page = materialRepository.findByNameContainingIgnoreCase(keyword, pageable);
    return page.map(materialMapper::toDto);
  }
}
