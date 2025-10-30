package com.example.nikonbe.modules.product_detail.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceAlreadyExistsException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailCreateDTO;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailUpdateDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.product_detail.mapper.ProductDetailMapper;
import com.example.nikonbe.modules.product_detail.repository.ProductDetailRepository;
import com.example.nikonbe.modules.product_detail.service.interF.ProductDetailService;
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
public class ProductDetailServiceImpl implements ProductDetailService {

  private final ProductDetailRepository repository;
  private final ProductDetailMapper mapper;

  @Override
  public ProductDetailResponseDTO create(ProductDetailCreateDTO dto) {
    if (repository.existsBySkuAndIdNot(dto.getSku(), 0)) {
      throw new ResourceAlreadyExistsException("SKU đã tồn tại");
    }
    ProductDetail entity = mapper.toEntity(dto);
    ProductDetail saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  public ProductDetailResponseDTO update(Integer id, ProductDetailUpdateDTO dto) {
    ProductDetail entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", "id", id));
    if (dto.getSku() != null && repository.existsBySkuAndIdNot(dto.getSku(), id)) {
      throw new ResourceAlreadyExistsException("SKU đã tồn tại");
    }
    mapper.updateEntityFromDto(dto, entity);
    ProductDetail updated = repository.save(entity);
    return mapper.toDto(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDetailResponseDTO getById(Integer id) {
    ProductDetail entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", "id", id));
    return mapper.toDto(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductDetailResponseDTO> getAll(
      Status status, Integer productId, Integer colorId, Integer capacityId, Pageable pageable) {
    Page<ProductDetail> page =
        repository.findAllWithFilters(status, productId, colorId, capacityId, pageable);
    return page.map(mapper::toDto);
  }

  @Override
  public void delete(Integer id) {
    ProductDetail entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", "id", id));
    entity.setStatus(Status.DELETED);
    repository.save(entity);
  }
}
