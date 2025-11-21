package com.example.nikonbe.modules.product.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceAlreadyExistsException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.attributes.brand.mapper.BrandMapper;
import com.example.nikonbe.modules.attributes.capacity.mapper.CapacityMapper;
import com.example.nikonbe.modules.attributes.category.mapper.CategoryMapper;
import com.example.nikonbe.modules.attributes.color.mapper.ColorMapper;
import com.example.nikonbe.modules.attributes.material.mapper.MaterialMapper;
import com.example.nikonbe.modules.attributes.strape_type.mapper.StrapTypeMapper;
import com.example.nikonbe.modules.color_image.entity.ColorImage;
import com.example.nikonbe.modules.color_image.mapper.ColorImageMapper;
import com.example.nikonbe.modules.color_image.repository.ColorImageRepository;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductDetailFullResponseDTO;
import com.example.nikonbe.modules.product.dto.response.ProductListingResponseDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product.mapper.ProductMapper;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.product.service.interF.ProductService;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailListingResponseDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailWithImageResponseDTO;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.product_detail.repository.ProductDetailRepository;
import com.example.nikonbe.modules.product_feature.mapper.ProductFeatureMapper;
import com.example.nikonbe.modules.product_feature.repository.ProductFeatureRepository;
import com.example.nikonbe.modules.product_tag.mapper.ProductTagMapper;
import com.example.nikonbe.modules.product_tag.repository.ProductTagRepository;
import com.example.nikonbe.modules.promotion.dto.response.PromotionDiscountResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.promotion.mapper.PromotionMapper;
import com.example.nikonbe.modules.promotion.service.interF.PromotionService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  private final ColorImageRepository colorImageRepository;
  private final ColorImageMapper colorImageMapper;
  private final ProductTagRepository productTagRepository;
  private final ProductTagMapper productTagMapper;
  private final ProductFeatureRepository productFeatureRepository;
  private final ProductFeatureMapper productFeatureMapper;
  private final ProductDetailRepository productDetailRepository;
  private final BrandMapper brandMapper;
  private final CategoryMapper categoryMapper;
  private final MaterialMapper materialMapper;
  private final StrapTypeMapper strapTypeMapper;
  private final ColorMapper colorMapper;
  private final CapacityMapper capacityMapper;
  private final PromotionMapper promotionMapper;
  private final PromotionService promotionService;

  private ProductResponseDTO enrichWithRelationships(ProductResponseDTO dto) {
    List<com.example.nikonbe.modules.color_image.entity.ColorImage> colorImages =
        colorImageRepository.findByProductIdWithDetails(dto.getId());
    dto.setColorImages(colorImageMapper.toDtoList(colorImages));

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
      String keyword,
      Status status,
      Integer categoryId,
      Integer brandId,
      Integer materialId,
      Integer strapTypeId,
      Pageable pageable) {
    Page<Product> page =
        repository.findAllWithFilters(
            keyword, status, categoryId, brandId, materialId, strapTypeId, pageable);
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
      List<com.example.nikonbe.modules.color_image.entity.ColorImage> allColorImages =
          colorImageRepository.findByProductIdInWithDetails(productIds);
      java.util.Map<
              Integer,
              List<com.example.nikonbe.modules.color_image.dto.response.ColorImageResponseDTO>>
          colorImagesMap =
              allColorImages.stream()
                  .collect(
                      Collectors.groupingBy(
                          img -> img.getProduct().getId(),
                          Collectors.mapping(colorImageMapper::toDto, Collectors.toList())));
      dtoPage
          .getContent()
          .forEach(dto -> dto.setColorImages(colorImagesMap.getOrDefault(dto.getId(), List.of())));

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

  @Override
  @Transactional(readOnly = true)
  public Page<ProductListingResponseDTO> getProductListings(
      String keyword,
      List<Integer> brandIds,
      List<Integer> strapTypeIds,
      List<Integer> materialIds,
      List<Integer> categoryIds,
      List<Integer> colorIds,
      List<Integer> capacityIds,
      List<Integer> tagIds,
      List<Integer> featureIds,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Boolean hasPromotion,
      Pageable pageable) {

    Page<Product> allProducts =
        repository.findByAdvancedFilters(
            keyword,
            brandIds != null && !brandIds.isEmpty() ? brandIds : null,
            strapTypeIds != null && !strapTypeIds.isEmpty() ? strapTypeIds : null,
            materialIds != null && !materialIds.isEmpty() ? materialIds : null,
            categoryIds != null && !categoryIds.isEmpty() ? categoryIds : null,
            Status.ACTIVE,
            Pageable.unpaged());

    List<ProductListingResponseDTO> allValidProducts =
        allProducts.stream()
            .map(this::buildEnhancedProductListingDTO)
            .filter(Objects::nonNull)
            .filter(dto -> hasValidVariants(dto.getVariants()))
            .filter(
                dto -> isValidPriceRange(dto.getMinPrice(), dto.getMaxPrice(), minPrice, maxPrice))
            .filter(dto -> hasMatchingColors(dto.getProductId(), colorIds))
            .filter(dto -> hasMatchingCapacities(dto.getProductId(), capacityIds))
            .filter(dto -> hasMatchingTags(dto.getProductId(), tagIds))
            .filter(dto -> hasMatchingFeatures(dto.getProductId(), featureIds))
            .filter(dto -> hasMatchingPromotion(dto.getProductId(), hasPromotion))
            .collect(Collectors.toList());

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), allValidProducts.size());
    List<ProductListingResponseDTO> pageContent =
        start >= allValidProducts.size() ? new ArrayList<>() : allValidProducts.subList(start, end);

    return new PageImpl<>(pageContent, pageable, allValidProducts.size());
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDetailFullResponseDTO getProductDetail(Integer productId) {
    Product product =
        repository
            .findByIdWithRelationships(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

    List<String> tags = getProductTags(productId);
    List<String> features = getProductFeatures(productId);
    List<ProductDetailWithImageResponseDTO> variants = getProductVariantsWithImage(productId);

    BigDecimal minPrice =
        productDetailRepository.findMinPriceByProductIdAndStatus(productId, Status.ACTIVE);
    BigDecimal maxPrice =
        productDetailRepository.findMaxPriceByProductIdAndStatus(productId, Status.ACTIVE);

    List<PromotionResponseDTO> availablePromotions =
        promotionService.getPromotionsForProduct(productId.toString());
    BigDecimal minPriceDiscount = minPrice != null ? minPrice : BigDecimal.ZERO;

    return ProductDetailFullResponseDTO.builder()
        .productId(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .dimensions(product.getDimensions())
        .weight(product.getWeight())
        .waterproofRating(product.getWaterproofRating())
        .brand(product.getBrand() != null ? brandMapper.toDto(product.getBrand()) : null)
        .strapType(
            product.getStrapType() != null ? strapTypeMapper.toDto(product.getStrapType()) : null)
        .material(
            product.getMaterial() != null ? materialMapper.toDto(product.getMaterial()) : null)
        .category(
            product.getCategory() != null ? categoryMapper.toDto(product.getCategory()) : null)
        .tags(tags)
        .features(features)
        .variants(variants)
        .minPrice(minPrice != null ? minPrice : BigDecimal.ZERO)
        .maxPrice(maxPrice != null ? maxPrice : BigDecimal.ZERO)
        .minPriceDiscount(minPriceDiscount)
        .availablePromotions(availablePromotions)
        .build();
  }

  private ProductListingResponseDTO buildEnhancedProductListingDTO(Product product) {
    List<ProductDetail> activeVariants =
        productDetailRepository.findByProductIdAndStatus(product.getId(), Status.ACTIVE).stream()
            .filter(variant -> variant.getStock() != null && variant.getStock() > 0)
            .collect(Collectors.toList());

    if (activeVariants.isEmpty()) {
      return null;
    }

    List<String> tags = getProductTags(product.getId());
    List<String> features = getProductFeatures(product.getId());

    List<PromotionResponseDTO> availablePromotions =
        promotionService.getPromotionsForProduct(product.getId().toString());

    List<ProductDetailListingResponseDTO> variantDTOs =
        buildVariantListingDTOs(activeVariants, availablePromotions);

    variantDTOs.sort(Comparator.comparing(ProductDetailListingResponseDTO::getFinalPrice));

    int maxVariantsToReturn = 10;
    if (variantDTOs.size() > maxVariantsToReturn) {
      variantDTOs = variantDTOs.subList(0, maxVariantsToReturn);
    }

    for (int i = 0; i < variantDTOs.size(); i++) {
      variantDTOs.get(i).setIsPrimary(i == 0);
      variantDTOs.get(i).setSortOrder(i + 1);
    }

    ProductDetailListingResponseDTO primaryVariant = variantDTOs.get(0);

    BigDecimal minPrice =
        variantDTOs.stream()
            .map(ProductDetailListingResponseDTO::getOriginalPrice)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);

    BigDecimal maxPrice =
        variantDTOs.stream()
            .map(ProductDetailListingResponseDTO::getOriginalPrice)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);

    BigDecimal bestDiscountPrice = primaryVariant.getFinalPrice();

    return ProductListingResponseDTO.builder()
        .productId(product.getId())
        .productName(product.getName())
        .description(product.getDescription())
        .dimensions(product.getDimensions())
        .weight(product.getWeight())
        .waterproofRating(product.getWaterproofRating())
        .brand(product.getBrand() != null ? brandMapper.toDto(product.getBrand()) : null)
        .strapType(
            product.getStrapType() != null ? strapTypeMapper.toDto(product.getStrapType()) : null)
        .material(
            product.getMaterial() != null ? materialMapper.toDto(product.getMaterial()) : null)
        .category(
            product.getCategory() != null ? categoryMapper.toDto(product.getCategory()) : null)
        .tags(tags)
        .features(features)
        .minPrice(minPrice)
        .maxPrice(maxPrice)
        .bestDiscountPrice(bestDiscountPrice)
        .bestPromotionId(primaryVariant.getPromotionId())
        .bestPromotionName(primaryVariant.getPromotionName())
        .bestPromotionType(primaryVariant.getPromotionType())
        .bestPromotionValue(primaryVariant.getPromotionValue())
        .variants(variantDTOs)
        .primaryVariant(primaryVariant)
        .build();
  }

  private List<ProductDetailListingResponseDTO> buildVariantListingDTOs(
      List<ProductDetail> variants, List<PromotionResponseDTO> availablePromotions) {

    return variants.stream()
        .map(
            variant -> {
              PromotionResponseDTO bestPromotion = null;
              if (variant.getPromotion() != null) {
                bestPromotion = promotionMapper.toDto(variant.getPromotion());
              }

              BigDecimal discountPrice = calculateDiscountPrice(variant.getPrice(), bestPromotion);
              BigDecimal discountAmount = variant.getPrice().subtract(discountPrice);

              String thumbnailImage = getVariantThumbnailImage(variant);

              Integer reservedStock =
                  variant.getReservedStock() != null ? variant.getReservedStock() : 0;
              Integer stock = variant.getStock() != null ? variant.getStock() : 0;
              Integer availableStock = Math.max(0, stock - reservedStock);

              return ProductDetailListingResponseDTO.builder()
                  .variantId(variant.getId())
                  .sku(variant.getSku())
                  .stock(stock)
                  .reservedStock(reservedStock)
                  .availableStock(availableStock)
                  .color(variant.getColor() != null ? colorMapper.toDto(variant.getColor()) : null)
                  .capacity(
                      variant.getCapacity() != null
                          ? capacityMapper.toDto(variant.getCapacity())
                          : null)
                  .originalPrice(variant.getPrice())
                  .discountPrice(discountPrice)
                  .finalPrice(discountPrice)
                  .promotionId(bestPromotion != null ? bestPromotion.getId() : null)
                  .promotionName(bestPromotion != null ? bestPromotion.getName() : null)
                  .promotionType(bestPromotion != null ? bestPromotion.getDiscountType() : null)
                  .promotionValue(bestPromotion != null ? bestPromotion.getDiscountValue() : null)
                  .discountAmount(discountAmount)
                  .thumbnailImage(thumbnailImage)
                  .isPrimary(false)
                  .sortOrder(0)
                  .build();
            })
        .collect(Collectors.toList());
  }

  private String getVariantThumbnailImage(ProductDetail variant) {
    if (variant == null || variant.getColor() == null || variant.getProduct() == null) {
      return null;
    }

    Integer productId = variant.getProduct().getId();
    Integer colorId = variant.getColor().getId();

    return colorImageRepository
        .findByProductIdAndColorId(productId, colorId)
        .map(ColorImage::getImageUrl)
        .orElse(null);
  }

  private List<String> getProductTags(Integer productId) {
    return productTagRepository.findByProductId(productId).stream()
        .map(pt -> pt.getTag().getName())
        .collect(Collectors.toList());
  }

  private List<String> getProductFeatures(Integer productId) {
    return productFeatureRepository.findByProductId(productId).stream()
        .map(pf -> pf.getFeature().getName())
        .collect(Collectors.toList());
  }

  private List<ProductDetailWithImageResponseDTO> getProductVariantsWithImage(Integer productId) {
    List<ProductDetail> variants =
        productDetailRepository.findByProductIdAndStatus(productId, Status.ACTIVE);

    Map<Integer, String> colorImageMap = getColorImageMap(productId);

    List<PromotionResponseDTO> availablePromotions =
        promotionService.getPromotionsForProduct(productId.toString());

    return variants.stream()
        .map(variant -> buildProductDetailWithImageDTO(variant, colorImageMap, availablePromotions))
        .collect(Collectors.toList());
  }

  private Map<Integer, String> getColorImageMap(Integer productId) {
    return colorImageRepository.findByProductId(productId).stream()
        .collect(
            Collectors.toMap(
                colorImage -> colorImage.getColor().getId(),
                ColorImage::getImageUrl,
                (existing, replacement) -> existing));
  }

  private ProductDetailWithImageResponseDTO buildProductDetailWithImageDTO(
      ProductDetail variant,
      Map<Integer, String> colorImageMap,
      List<PromotionResponseDTO> availablePromotions) {

    PromotionResponseDTO bestPromotion = null;
    if (variant.getPromotion() != null) {
      bestPromotion = promotionMapper.toDto(variant.getPromotion());
    }

    BigDecimal discountPrice = calculateDiscountPrice(variant.getPrice(), bestPromotion);
    BigDecimal discountAmount = variant.getPrice().subtract(discountPrice);

    String thumbnailImage = null;
    if (variant.getColor() != null && colorImageMap.containsKey(variant.getColor().getId())) {
      thumbnailImage = colorImageMap.get(variant.getColor().getId());
    }

    Integer reservedStock = variant.getReservedStock() != null ? variant.getReservedStock() : 0;
    Integer stock = variant.getStock() != null ? variant.getStock() : 0;
    Integer availableStock = Math.max(0, stock - reservedStock);

    return ProductDetailWithImageResponseDTO.builder()
        .id(variant.getId())
        .sku(variant.getSku())
        .stock(stock)
        .reservedStock(reservedStock)
        .availableStock(availableStock)
        .productName(variant.getProduct() != null ? variant.getProduct().getName() : null)
        .color(variant.getColor() != null ? colorMapper.toDto(variant.getColor()) : null)
        .capacity(
            variant.getCapacity() != null ? capacityMapper.toDto(variant.getCapacity()) : null)
        .price(variant.getPrice())
        .status(variant.getStatus())
        .promotion(bestPromotion)
        .thumbnailImage(thumbnailImage)
        .discountPrice(discountPrice)
        .discountAmount(discountAmount)
        .build();
  }

  private BigDecimal calculateDiscountPrice(BigDecimal price, PromotionResponseDTO promotion) {
    if (promotion == null || price == null) {
      return price;
    }

    PromotionDiscountResponseDTO discountResponse =
        promotionService.calculateDiscountAmount(promotion.getId(), price);
    if (discountResponse.getCanUse()) {
      return price.subtract(discountResponse.getDiscountAmount());
    }

    return price;
  }

  private boolean hasValidVariants(List<ProductDetailListingResponseDTO> variants) {
    return variants != null && !variants.isEmpty();
  }

  private boolean isValidPriceRange(
      BigDecimal minPrice,
      BigDecimal maxPrice,
      BigDecimal filterMinPrice,
      BigDecimal filterMaxPrice) {
    if (minPrice == null || maxPrice == null) return false;

    boolean validMin = filterMinPrice == null || maxPrice.compareTo(filterMinPrice) >= 0;
    boolean validMax = filterMaxPrice == null || minPrice.compareTo(filterMaxPrice) <= 0;

    return validMin && validMax;
  }

  private boolean hasMatchingColors(Integer productId, List<Integer> colorIds) {
    if (colorIds == null || colorIds.isEmpty()) return true;

    return productDetailRepository.findByProductIdAndStatus(productId, Status.ACTIVE).stream()
        .anyMatch(
            variant -> variant.getColor() != null && colorIds.contains(variant.getColor().getId()));
  }

  private boolean hasMatchingCapacities(Integer productId, List<Integer> capacityIds) {
    if (capacityIds == null || capacityIds.isEmpty()) return true;

    return productDetailRepository.findByProductIdAndStatus(productId, Status.ACTIVE).stream()
        .anyMatch(
            variant ->
                variant.getCapacity() != null
                    && capacityIds.contains(variant.getCapacity().getId()));
  }

  private boolean hasMatchingTags(Integer productId, List<Integer> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) return true;

    return productTagRepository.findByProductId(productId).stream()
        .anyMatch(pt -> tagIds.contains(pt.getTag().getId()));
  }

  private boolean hasMatchingFeatures(Integer productId, List<Integer> featureIds) {
    if (featureIds == null || featureIds.isEmpty()) return true;

    return productFeatureRepository.findByProductId(productId).stream()
        .anyMatch(pf -> featureIds.contains(pf.getFeature().getId()));
  }

  private boolean hasMatchingPromotion(Integer productId, Boolean hasPromotion) {
    if (hasPromotion == null) return true;

    return productDetailRepository.findByProductIdAndStatus(productId, Status.ACTIVE).stream()
        .anyMatch(
            variant ->
                hasPromotion ? variant.getPromotion() != null : variant.getPromotion() == null);
  }
}
