package com.example.nikonbe.modules.product_tag.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.product_tag.dto.request.ProductTagCreateDTO;
import com.example.nikonbe.modules.product_tag.dto.request.ProductTagUpdateDTO;
import com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO;
import com.example.nikonbe.modules.product_tag.entity.ProductTag;
import com.example.nikonbe.modules.product_tag.entity.ProductTagId;
import com.example.nikonbe.modules.product_tag.mapper.ProductTagMapper;
import com.example.nikonbe.modules.product_tag.repository.ProductTagRepository;
import com.example.nikonbe.modules.product_tag.service.interF.ProductTagService;
import com.example.nikonbe.modules.tag.entity.Tag;
import com.example.nikonbe.modules.tag.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductTagServiceImpl implements ProductTagService {

  private final ProductTagRepository productTagRepository;
  private final ProductRepository productRepository;
  private final TagRepository tagRepository;
  private final ProductTagMapper productTagMapper;

  @Transactional
  @Override
  public ProductTagResponseDTO addTag(Integer productId, ProductTagCreateDTO dto) {
    validateProductAndTag(productId, dto.getTagId());

    if (productTagRepository.existsByProductIdAndTagId(productId, dto.getTagId())) {
      throw new ValidationException("Sản phẩm này đã có thẻ này.");
    }

    ProductTag productTag =
        ProductTag.builder()
            .product(Product.builder().id(productId).build())
            .tag(Tag.builder().id(dto.getTagId()).build())
            .build();

    ProductTag savedProductTag = productTagRepository.save(productTag);
    return productTagMapper.toDto(savedProductTag);
  }

  @Transactional
  @Override
  public List<ProductTagResponseDTO> updateTags(Integer productId, ProductTagUpdateDTO dto) {
    validateProductExists(productId);

    for (Integer tagId : dto.getTagIds()) {
      validateTagExists(tagId);
    }

    productTagRepository.deleteByProductId(productId);

    List<ProductTag> newTags =
        dto.getTagIds().stream()
            .map(
                tagId ->
                    ProductTag.builder()
                        .product(Product.builder().id(productId).build())
                        .tag(Tag.builder().id(tagId).build())
                        .build())
            .collect(Collectors.toList());

    productTagRepository.saveAll(newTags);
    return productTagMapper.toDtoList(newTags);
  }

  @Override
  public List<ProductTagResponseDTO> getByProductId(Integer productId) {
    validateProductExists(productId);
    List<ProductTag> productTags = productTagRepository.findByProductId(productId);
    return productTagMapper.toDtoList(productTags);
  }

  @Override
  public List<ProductTagResponseDTO> getByTagId(Integer tagId) {
    validateTagExists(tagId);
    List<ProductTag> productTags = productTagRepository.findByTagId(tagId);
    return productTagMapper.toDtoList(productTags);
  }

  @Transactional
  @Override
  public void removeTag(Integer productId, Integer tagId) {
    validateProductAndTag(productId, tagId);

    if (!productTagRepository.existsByProductIdAndTagId(productId, tagId)) {
      throw new ResourceNotFoundException(
          "Liên kết sản phẩm-thẻ", "productId và tagId", productId + ", " + tagId);
    }

    productTagRepository.deleteById(new ProductTagId(productId, tagId));
  }

  @Transactional
  @Override
  public void removeAllTags(Integer productId) {
    validateProductExists(productId);
    productTagRepository.deleteByProductId(productId);
  }

  private void validateProductExists(Integer productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Sản phẩm", "id", productId);
    }
  }

  private void validateTagExists(Integer tagId) {
    Tag tag =
        tagRepository
            .findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Thẻ", "id", tagId));

    if (tag.getStatus().getValue() == 0) {
      throw new ValidationException("Thẻ với ID " + tagId + " không hoạt động.");
    }
  }

  private void validateProductAndTag(Integer productId, Integer tagId) {
    validateProductExists(productId);
    validateTagExists(tagId);
  }
}
