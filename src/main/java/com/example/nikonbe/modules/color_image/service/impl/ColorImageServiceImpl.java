package com.example.nikonbe.modules.color_image.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.common.helper.cloudinary.service.ImageUploadService;
import com.example.nikonbe.modules.attributes.color.repository.ColorRepository;
import com.example.nikonbe.modules.color_image.dto.request.ColorImageCreateDTO;
import com.example.nikonbe.modules.color_image.dto.request.ColorImageUpdateDTO;
import com.example.nikonbe.modules.color_image.dto.response.ColorImageResponseDTO;
import com.example.nikonbe.modules.color_image.entity.ColorImage;
import com.example.nikonbe.modules.color_image.mapper.ColorImageMapper;
import com.example.nikonbe.modules.color_image.repository.ColorImageRepository;
import com.example.nikonbe.modules.color_image.service.interF.ColorImageService;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColorImageServiceImpl implements ColorImageService {

  private final ColorImageRepository colorImageRepository;
  private final ColorImageMapper colorImageMapper;
  private final ProductRepository productRepository;
  private final ColorRepository colorRepository;
  private final ImageUploadService imageUploadService;

  @Override
  @Transactional
  public ColorImageResponseDTO create(ColorImageCreateDTO dto) {
    if (!productRepository.existsById(dto.getProductId())) {
      throw new ResourceNotFoundException("Product", "id", dto.getProductId());
    }
    if (!colorRepository.existsById(dto.getColorId())) {
      throw new ResourceNotFoundException("Color", "id", dto.getColorId());
    }
    if (colorImageRepository.existsByProductIdAndColorId(dto.getProductId(), dto.getColorId())) {
      throw new ValidationException("Hình ảnh cho sản phẩm và màu sắc này đã tồn tại.");
    }

    ColorImage colorImage = colorImageMapper.toEntity(dto);
    ColorImage savedColorImage = colorImageRepository.save(colorImage);
    return colorImageMapper.toDto(savedColorImage);
  }

  @Override
  @Transactional
  public ColorImageResponseDTO update(Integer id, ColorImageUpdateDTO dto) {
    ColorImage colorImage =
        colorImageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ColorImage", "id", id));

    if (!productRepository.existsById(dto.getProductId())) {
      throw new ResourceNotFoundException("Product", "id", dto.getProductId());
    }
    if (!colorRepository.existsById(dto.getColorId())) {
      throw new ResourceNotFoundException("Color", "id", dto.getColorId());
    }

    Optional<ColorImage> existingColorImage =
        colorImageRepository.findByProductIdAndColorId(dto.getProductId(), dto.getColorId());

    if (existingColorImage.isPresent() && !existingColorImage.get().getId().equals(id)) {
      throw new ValidationException("Hình ảnh cho sản phẩm và màu sắc này đã tồn tại.");
    }

    String oldImageUrl = colorImage.getImageUrl();
    colorImageMapper.updateEntityFromDto(dto, colorImage);
    ColorImage updatedColorImage = colorImageRepository.save(colorImage);

    if (!oldImageUrl.equals(dto.getImageUrl())) {
      try {
        imageUploadService.deleteImage(oldImageUrl);
        log.info("Deleted old image from Cloudinary: {}", oldImageUrl);
      } catch (Exception e) {
        log.warn("Failed to delete old image from Cloudinary: {}", e.getMessage());
      }
    }

    return colorImageMapper.toDto(updatedColorImage);
  }

  @Override
  public ColorImageResponseDTO getById(Integer id) {
    ColorImage colorImage =
        colorImageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ColorImage", "id", id));
    return colorImageMapper.toDto(colorImage);
  }

  @Override
  public List<ColorImageResponseDTO> getAll() {
    return colorImageMapper.toDtoList(colorImageRepository.findAll());
  }

  @Override
  public List<ColorImageResponseDTO> getByProductId(Integer productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", "id", productId);
    }
    List<ColorImage> colorImages = colorImageRepository.findByProductIdWithDetails(productId);
    return colorImageMapper.toDtoList(colorImages);
  }

  @Override
  public ColorImageResponseDTO getByProductIdAndColorId(Integer productId, Integer colorId) {
    ColorImage colorImage =
        colorImageRepository
            .findByProductIdAndColorIdWithDetails(productId, colorId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "ColorImage", "productId and colorId", productId + " and " + colorId));
    return colorImageMapper.toDto(colorImage);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    ColorImage colorImage =
        colorImageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ColorImage", "id", id));

    try {
      if (colorImage.getImageUrl() != null && !colorImage.getImageUrl().isEmpty()) {
        imageUploadService.deleteImage(colorImage.getImageUrl());
        log.info("Deleted image from Cloudinary: {}", colorImage.getImageUrl());
      }
    } catch (Exception e) {
      log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
    }

    colorImageRepository.deleteById(id);
    log.info("Deleted ColorImage with id: {}", id);
  }

  @Override
  @Transactional
  public void deleteByProductAndColor(Integer productId, Integer colorId) {
    ColorImage colorImage =
        colorImageRepository
            .findByProductIdAndColorId(productId, colorId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "ColorImage", "productId and colorId", productId + " and " + colorId));

    try {
      if (colorImage.getImageUrl() != null && !colorImage.getImageUrl().isEmpty()) {
        imageUploadService.deleteImage(colorImage.getImageUrl());
        log.info("Deleted image from Cloudinary: {}", colorImage.getImageUrl());
      }
    } catch (Exception e) {
      log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
    }

    colorImageRepository.deleteByProductIdAndColorId(productId, colorId);
    log.info("Deleted ColorImage for productId: {} and colorId: {}", productId, colorId);
  }
}
