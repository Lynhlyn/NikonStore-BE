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
import com.example.nikonbe.modules.product_feature.mapper.ProductFeatureMapper;
import com.example.nikonbe.modules.product_feature.repository.ProductFeatureRepository;
import com.example.nikonbe.modules.product_image.mapper.ProductImageMapper;
import com.example.nikonbe.modules.product_image.repository.ProductImageRepository;
import com.example.nikonbe.modules.product_tag.mapper.ProductTagMapper;
import com.example.nikonbe.modules.product_tag.repository.ProductTagRepository;
import java.util.List;
import java.util.stream.Collectors;
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
  private final ProductImageRepository productImageRepository;
  private final ProductImageMapper productImageMapper;
  private final ProductTagRepository productTagRepository;
  private final ProductTagMapper productTagMapper;
  private final ProductFeatureRepository productFeatureRepository;
  private final ProductFeatureMapper productFeatureMapper;

  private ProductResponseDTO enrichWithRelationships(ProductResponseDTO dto) {
    List<com.example.nikonbe.modules.product_image.entity.ProductImage> images =
        productImageRepository.findByProductIdOrderBySortOrderAsc(dto.getId());
    dto.setImages(productImageMapper.toDtoList(images));

    List<com.example.nikonbe.modules.product_tag.entity.ProductTag> tags =
        productTagRepository.findByProductId(dto.getId());
    dto.setTags(productTagMapper.toDtoList(tags));

    List<com.example.nikonbe.modules.product_feature.entity.ProductFeature> features =
        productFeatureRepository.findByProductId(dto.getId());
    dto.setFeatures(productFeatureMapper.toDtoList(features));

    return dto;
  }

  @Override
  public ProductResponseDTO create(ProductCreateDTO dto) {
    if (repository.existsByNameAndIdNot(dto.getName(), 0)) {
      throw new ResourceAlreadyExistsException(
          "Product với tên '" + dto.getName() + "' đã tồn tại");
    }
    Product entity = mapper.toEntity(dto);
    Product saved = repository.save(entity);
    Product savedWithRelationships =
        repository
            .findByIdWithRelationships(saved.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", saved.getId()));
    return enrichWithRelationships(mapper.toDto(savedWithRelationships));
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
    Product updatedWithRelationships =
        repository
            .findByIdWithRelationships(updated.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", updated.getId()));
    return enrichWithRelationships(mapper.toDto(updatedWithRelationships));
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponseDTO getById(Integer id) {
    Product entity =
        repository
            .findByIdWithRelationships(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    return enrichWithRelationships(mapper.toDto(entity));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDTO> getAll(
      Status status, Integer categoryId, Integer brandId, Pageable pageable) {
    Page<Product> page = repository.findAllWithFilters(status, categoryId, brandId, pageable);
    List<Integer> productIds =
        page.getContent().stream().map(Product::getId).collect(Collectors.toList());
    List<Product> productsWithRelationships =
        productIds.isEmpty() ? List.of() : repository.findAllWithRelationshipsByIds(productIds);
    java.util.Map<Integer, Product> productMap =
        productsWithRelationships.stream()
            .collect(Collectors.toMap(Product::getId, product -> product));
    Page<ProductResponseDTO> dtoPage =
        page.map(
            product -> {
              Product productWithRelationships = productMap.get(product.getId());
              return productWithRelationships != null
                  ? mapper.toDto(productWithRelationships)
                  : mapper.toDto(product);
            });
    if (!productIds.isEmpty()) {
      List<com.example.nikonbe.modules.product_image.entity.ProductImage> allImages =
          productImageRepository.findByProductIdInOrderByProductIdAndSortOrderAsc(productIds);
      java.util.Map<
              Integer,
              List<com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO>>
          imagesMap =
              allImages.stream()
                  .collect(
                      Collectors.groupingBy(
                          img -> img.getProduct().getId(),
                          Collectors.mapping(productImageMapper::toDto, Collectors.toList())));
      dtoPage
          .getContent()
          .forEach(dto -> dto.setImages(imagesMap.getOrDefault(dto.getId(), List.of())));

      List<com.example.nikonbe.modules.product_tag.entity.ProductTag> allTags =
          productTagRepository.findByProductIdIn(productIds);
      java.util.Map<
              Integer,
              List<com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO>>
          tagsMap =
              allTags.stream()
                  .collect(
                      Collectors.groupingBy(
                          tag -> tag.getProduct().getId(),
                          Collectors.mapping(productTagMapper::toDto, Collectors.toList())));
      dtoPage
          .getContent()
          .forEach(dto -> dto.setTags(tagsMap.getOrDefault(dto.getId(), List.of())));

      List<com.example.nikonbe.modules.product_feature.entity.ProductFeature> allFeatures =
          productFeatureRepository.findByProductIdIn(productIds);
      java.util.Map<
              Integer,
              List<
                  com.example.nikonbe.modules.product_feature.dto.response
                      .ProductFeatureResponseDTO>>
          featuresMap =
              allFeatures.stream()
                  .collect(
                      Collectors.groupingBy(
                          feature -> feature.getProduct().getId(),
                          Collectors.mapping(productFeatureMapper::toDto, Collectors.toList())));
      dtoPage
          .getContent()
          .forEach(dto -> dto.setFeatures(featuresMap.getOrDefault(dto.getId(), List.of())));
    }
    return dtoPage;
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
