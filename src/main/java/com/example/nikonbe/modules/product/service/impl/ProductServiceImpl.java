package com.example.nikonbe.modules.product.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceAlreadyExistsException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product.mapper.ProductMapper;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.product.service.interF.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepository repository;
  private final ProductMapper mapper;

  @Override
  public ProductResponseDTO create(ProductCreateDTO dto) {
    if (repository.existsByNameAndIdNot(dto.getName(), 0)) {
      throw new ResourceAlreadyExistsException(
          "Product với tên '" + dto.getName() + "' đã tồn tại");
    }
    Product entity = mapper.toEntity(dto);
    Product saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  public ProductResponseDTO update(Integer id, ProductUpdateDTO dto) {
    Product entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    if (dto.getName() != null && repository.existsByNameAndIdNot(dto.getName(), id)) {
      throw new ResourceAlreadyExistsException(
          "Product với tên '" + dto.getName() + "' đã tồn tại");
    }
    mapper.updateEntityFromDto(dto, entity);
    Product updated = repository.save(entity);
    return mapper.toDto(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponseDTO getById(Integer id) {
    Product entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    return mapper.toDto(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDTO> getAll(
      Status status, Integer categoryId, Integer brandId, Pageable pageable) {
    Page<Product> page = repository.findAllWithFilters(status, categoryId, brandId, pageable);
    return page.map(mapper::toDto);
  }

  @Override
  public void delete(Integer id) {
    Product entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    entity.setStatus(Status.DELETED);
    repository.save(entity);
  }
}
