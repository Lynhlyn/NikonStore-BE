package com.example.nikonbe.modules.product_feature.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.feature.entity.Feature;
import com.example.nikonbe.modules.feature.repository.FeatureRepository;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.product_feature.dto.request.ProductFeatureCreateDTO;
import com.example.nikonbe.modules.product_feature.dto.request.ProductFeatureUpdateDTO;
import com.example.nikonbe.modules.product_feature.dto.response.ProductFeatureResponseDTO;
import com.example.nikonbe.modules.product_feature.entity.ProductFeature;
import com.example.nikonbe.modules.product_feature.mapper.ProductFeatureMapper;
import com.example.nikonbe.modules.product_feature.repository.ProductFeatureRepository;
import com.example.nikonbe.modules.product_feature.service.interF.ProductFeatureService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductFeatureServiceImpl implements ProductFeatureService {

  private final ProductFeatureRepository productFeatureRepository;
  private final ProductRepository productRepository;
  private final FeatureRepository featureRepository;
  private final ProductFeatureMapper productFeatureMapper;

  @Override
  public ProductFeatureResponseDTO addFeatureToProduct(
      Integer productId, ProductFeatureCreateDTO dto) {
    log.info("Adding feature {} to product {}", dto.getFeatureId(), productId);

    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + productId));

    Feature feature =
        featureRepository
            .findById(dto.getFeatureId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Feature not found with id: " + dto.getFeatureId()));

    if (productFeatureRepository.existsByProductIdAndFeatureId(productId, dto.getFeatureId())) {
      throw new ValidationException("Feature already exists for this product");
    }

    ProductFeature productFeature =
        ProductFeature.builder().product(product).feature(feature).build();

    productFeature = productFeatureRepository.save(productFeature);

    log.info("Successfully added feature {} to product {}", dto.getFeatureId(), productId);
    return productFeatureMapper.toDto(productFeature);
  }

  @Override
  public List<ProductFeatureResponseDTO> updateProductFeatures(
      Integer productId, ProductFeatureUpdateDTO dto) {
    log.info("Updating features for product {}", productId);

    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + productId));

    List<Feature> features = featureRepository.findAllById(dto.getFeatureIds());
    if (features.size() != dto.getFeatureIds().size()) {
      throw new ValidationException("Some features do not exist");
    }

    productFeatureRepository.deleteByProductId(productId);

    List<ProductFeature> productFeatures =
        dto.getFeatureIds().stream()
            .map(
                featureId -> {
                  Feature feature =
                      features.stream()
                          .filter(f -> f.getId().equals(featureId))
                          .findFirst()
                          .orElseThrow(
                              () ->
                                  new ResourceNotFoundException(
                                      "Feature not found with id: " + featureId));

                  return ProductFeature.builder().product(product).feature(feature).build();
                })
            .collect(Collectors.toList());

    productFeatures = productFeatureRepository.saveAll(productFeatures);

    log.info("Successfully updated features for product {}", productId);
    return productFeatureMapper.toDtoList(productFeatures);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductFeatureResponseDTO> getByProductId(Integer productId) {
    log.info("Getting features for product {}", productId);

    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product not found with id: " + productId);
    }

    List<ProductFeature> productFeatures = productFeatureRepository.findByProductId(productId);
    return productFeatureMapper.toDtoList(productFeatures);
  }

  @Override
  public void removeFeatureFromProduct(Integer productId, Integer featureId) {
    log.info("Removing feature {} from product {}", featureId, productId);

    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product not found with id: " + productId);
    }

    if (!featureRepository.existsById(featureId)) {
      throw new ResourceNotFoundException("Feature not found with id: " + featureId);
    }

    if (!productFeatureRepository.existsByProductIdAndFeatureId(productId, featureId)) {
      throw new ValidationException("Feature is not associated with this product");
    }

    productFeatureRepository.deleteByProductIdAndFeatureId(productId, featureId);

    log.info("Successfully removed feature {} from product {}", featureId, productId);
  }

  @Override
  public void removeAllFeaturesFromProduct(Integer productId) {
    log.info("Removing all features from product {}", productId);

    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product not found with id: " + productId);
    }

    productFeatureRepository.deleteByProductId(productId);

    log.info("Successfully removed all features from product {}", productId);
  }
}
