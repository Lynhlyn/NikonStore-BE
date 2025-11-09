package com.example.nikonbe.modules.product_image.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.helper.cloudinary.service.ImageUploadService;
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
  private final ImageUploadService imageUploadService;

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
    ProductImage entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", id));

    // Xóa ảnh trên Cloudinary trước khi xóa record
    try {
      if (entity.getImageUrl() != null && !entity.getImageUrl().isEmpty()) {
        imageUploadService.deleteImage(entity.getImageUrl());
        log.info("Deleted image from Cloudinary: {}", entity.getImageUrl());
      }
    } catch (Exception e) {
      log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
      // Tiếp tục xóa record trong DB dù có lỗi khi xóa trên Cloudinary
    }

    repository.deleteById(id);
    log.info("Deleted ProductImage with id: {}", id);
  }

  @Override
  public void deleteByProductId(Integer productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", "id", productId);
    }

    // Lấy danh sách ảnh trước khi xóa
    List<ProductImage> images = repository.findByProductIdOrderBySortOrderAsc(productId);

    // Xóa tất cả ảnh trên Cloudinary
    for (ProductImage image : images) {
      try {
        if (image.getImageUrl() != null && !image.getImageUrl().isEmpty()) {
          imageUploadService.deleteImage(image.getImageUrl());
          log.info("Deleted image from Cloudinary: {}", image.getImageUrl());
        }
      } catch (Exception e) {
        log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
        // Tiếp tục xóa các ảnh khác dù có lỗi
      }
    }

    repository.deleteByProductId(productId);
    log.info("Deleted all ProductImages for product id: {}", productId);
  }
}
