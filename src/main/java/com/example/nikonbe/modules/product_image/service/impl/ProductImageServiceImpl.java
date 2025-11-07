package com.example.nikonbe.modules.product_image.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageCreateDTO;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageUpdateDTO;
import com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO;
import com.example.nikonbe.modules.product_image.entity.ProductImage;
import com.example.nikonbe.modules.product_image.mapper.ProductImageMapper;
import com.example.nikonbe.modules.product_image.repository.ProductImageRepository;
import com.example.nikonbe.modules.product_image.service.interF.ProductImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

  private final ProductImageRepository repository;
  private final ProductRepository productRepository;
  private final ProductImageMapper mapper;

  @Override
  public ProductImageResponseDTO create(ProductImageCreateDTO dto) {
    if (!productRepository.existsById(dto.getProductId())) {
      throw new ResourceNotFoundException("Product", "id", dto.getProductId());
    }

    if (Boolean.TRUE.equals(dto.getIsPrimary())) {
      repository.unsetPrimaryByProductId(dto.getProductId());
    }

    ProductImage entity = mapper.toEntity(dto);
    ProductImage saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  public ProductImageResponseDTO update(Integer id, ProductImageUpdateDTO dto) {
    ProductImage entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", id));

    if (Boolean.TRUE.equals(dto.getIsPrimary()) && !entity.getIsPrimary()) {
      repository.unsetPrimaryByProductId(entity.getProduct().getId());
    }

    mapper.updateEntityFromDto(dto, entity);
    ProductImage updated = repository.save(entity);
    return mapper.toDto(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductImageResponseDTO getById(Integer id) {
    ProductImage entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", id));
    return mapper.toDto(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductImageResponseDTO> getByProductId(Integer productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", "id", productId);
    }
    List<ProductImage> images = repository.findByProductIdOrderBySortOrderAsc(productId);
    return mapper.toDtoList(images);
  }

  @Override
  public void delete(Integer id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("ProductImage", "id", id);
    }
    repository.deleteById(id);
  }

  @Override
  public void deleteByProductId(Integer productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", "id", productId);
    }
    repository.deleteByProductId(productId);
  }
}
