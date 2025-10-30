package com.example.nikonbe.modules.attributes.brand.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandCreateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandUpdateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.brand.entity.Brand;
import com.example.nikonbe.modules.attributes.brand.mapper.BrandMapper;
import com.example.nikonbe.modules.attributes.brand.repository.BrandRepository;
import com.example.nikonbe.modules.attributes.brand.service.interF.BrandService;
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
public class BrandServiceImpl implements BrandService {

  private final BrandRepository brandRepository;
  private final BrandMapper brandMapper;

  @Transactional
  @Override
  public BrandResponseDTO create(BrandCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (brandRepository.existsByName(dto.getName())) {
      errors.put("name", "Brand name already exists");
    }

    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Brand name is required");
    }

    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    Brand brand = brandMapper.toEntity(dto);
    Brand savedBrand = brandRepository.save(brand);
    return brandMapper.toDto(savedBrand);
  }

  @Transactional
  @Override
  public BrandResponseDTO update(Integer id, BrandUpdateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    Brand brand =
        brandRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));

    if (brandRepository.existsByNameAndIdNot(dto.getName(), id)) {
      errors.put("name", "Brand name already exists");
    }

    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      errors.put("name", "Brand name is required");
    }

    if (dto.getStatus() == null) {
      errors.put("status", "Status is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    brandMapper.updateEntityFromDto(dto, brand);
    Brand updatedBrand = brandRepository.save(brand);

    return brandMapper.toDto(updatedBrand);
  }

  @Transactional(readOnly = true)
  @Override
  public BrandResponseDTO getById(Integer id) {
    Brand brand =
        brandRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
    return brandMapper.toDto(brand);
  }

  @Transactional(readOnly = true)
  @Override
  public List<BrandResponseDTO> getAll() {
    List<Brand> brands = brandRepository.findAll();
    return brandMapper.toDtoList(brands);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<BrandResponseDTO> getAllPaginated(Pageable pageable) {
    Page<Brand> brandPage = brandRepository.findAll(pageable);
    return brandPage.map(brandMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public List<BrandResponseDTO> getAllByStatus(Status status) {
    List<Brand> brands = brandRepository.findByStatus(status);
    return brandMapper.toDtoList(brands);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<BrandResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    Page<Brand> brandPage = brandRepository.findByStatus(status, pageable);
    return brandPage.map(brandMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    Brand brand =
        brandRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));

    brand.setStatus(Status.DELETED);
    brandRepository.save(brand);
  }

  @Override
  public boolean existsByName(String name) {
    return brandRepository.existsByName(name);
  }
}
