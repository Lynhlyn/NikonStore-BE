package com.example.nikonbe.modules.promotion.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product.mapper.ProductMapper;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.product_detail.mapper.ProductDetailMapper;
import com.example.nikonbe.modules.product_detail.repository.ProductDetailRepository;
import com.example.nikonbe.modules.promotion.dto.request.PromotionCreateDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionSearchDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionUpdateDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionDiscountResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.promotion.entity.Promotion;
import com.example.nikonbe.modules.promotion.mapper.PromotionMapper;
import com.example.nikonbe.modules.promotion.repository.PromotionRepository;
import com.example.nikonbe.modules.promotion.service.interF.PromotionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionServiceImpl implements PromotionService {
  private final PromotionRepository promotionRepository;
  private final PromotionMapper promotionMapper;
  private final ProductDetailRepository productDetailRepository;
  private final ProductDetailMapper productDetailMapper;
  private final ProductMapper productMapper;

  @Override
  @Transactional
  public PromotionResponseDTO createPromotion(PromotionCreateDTO request) {
    log.info(
        "Creating promotion with name: {} and {} product details",
        request.getName(),
        request.getProductDetailIds() != null ? request.getProductDetailIds().size() : 0);

    Map<String, String> errors = validatePromotionData(request, null);
    if (!errors.isEmpty()) {
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }

    if (!CollectionUtils.isEmpty(request.getProductDetailIds())) {
      validateProductDetailIds(request.getProductDetailIds());
    }

    Promotion promotion = promotionMapper.toEntity(request);

    Status initialStatus = determineInitialStatus(promotion.getStartDate(), promotion.getEndDate());
    promotion.setStatus(initialStatus);

    Promotion savedPromotion = promotionRepository.save(promotion);

    if (!CollectionUtils.isEmpty(request.getProductDetailIds())) {
      applyPromotionToProductDetails(savedPromotion.getId(), request.getProductDetailIds());
    }

    log.info(
        "Created promotion successfully with ID: {} and status: {}",
        savedPromotion.getId(),
        savedPromotion.getStatus());

    return getPromotionByIdWithDetails(savedPromotion.getId());
  }

  @Override
  @Transactional
  public PromotionResponseDTO updatePromotion(Integer id, PromotionUpdateDTO request) {
    log.info(
        "Updating promotion with ID: {} and {} product details",
        id,
        request.getProductDetailIds() != null ? request.getProductDetailIds().size() : 0);

    Promotion promotion = findPromotionById(id);

    if (request.getCode() != null
        && !request.getCode().equals(promotion.getCode())
        && promotionRepository.existsByCodeAndIdNot(request.getCode(), id)) {
      Map<String, String> errors = new HashMap<>();
      errors.put("code", "Mã khuyến mãi đã tồn tại: " + request.getCode());
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }

    if (request.getProductDetailIds() != null) {
      if (!request.getProductDetailIds().isEmpty()) {
        validateProductDetailIds(request.getProductDetailIds());
      }

      removePromotionFromAllProductDetails(id);

      if (!request.getProductDetailIds().isEmpty()) {
        applyPromotionToProductDetails(id, request.getProductDetailIds());
      }
    }

    Status oldStatus = promotion.getStatus();
    promotionMapper.updateEntityFromDto(request, promotion);

    if (request.getStatus() != null) {
      promotion.setStatus(request.getStatus());
      log.info("Admin manually set promotion status to {} for ID: {}", request.getStatus(), id);
    } else {
      if (!Status.DELETED.equals(oldStatus)) {
        LocalDateTime startDate = promotion.getStartDate();
        LocalDateTime endDate = promotion.getEndDate();

        if (startDate != null && endDate != null) {
          Status newStatus =
              determinePromotionStatusForUpdate(promotion, LocalDateTime.now(), oldStatus);
          promotion.setStatus(newStatus);
          log.info(
              "Auto-updated promotion status from {} to {} for ID: {}", oldStatus, newStatus, id);
        }
      }
    }

    Promotion updatedPromotion = promotionRepository.save(promotion);

    log.info(
        "Updated promotion successfully with ID: {} from status {} to {}",
        id,
        oldStatus,
        updatedPromotion.getStatus());

    return getPromotionByIdWithDetails(id);
  }

  @Override
  @Transactional
  public void applyPromotionToProductDetails(Integer promotionId, List<Integer> productDetailIds) {
    log.info("Applying promotion {} to {} product details", promotionId, productDetailIds.size());

    if (CollectionUtils.isEmpty(productDetailIds)) {
      return;
    }

    Promotion promotion = findPromotionById(promotionId);

    List<ProductDetail> productDetails = productDetailRepository.findAllById(productDetailIds);

    if (productDetails.size() != productDetailIds.size()) {
      throw new ValidationException("Một số product detail không tồn tại", new HashMap<>());
    }

    for (ProductDetail productDetail : productDetails) {
      if (productDetail.getPromotion() != null
          && !productDetail.getPromotion().getId().equals(promotionId)) {
        log.warn(
            "ProductDetail {} đang áp dụng promotion khác: {}",
            productDetail.getId(),
            productDetail.getPromotion().getId());
      }

      productDetail.setPromotion(promotion);
    }

    productDetailRepository.saveAll(productDetails);
    log.info(
        "Applied promotion {} to {} product details successfully",
        promotionId,
        productDetails.size());
  }

  @Override
  @Transactional
  public void removePromotionFromAllProductDetails(Integer promotionId) {
    log.info("Removing promotion {} from all product details", promotionId);

    List<ProductDetail> productDetails = promotionRepository.findByPromotionId(promotionId);

    if (!CollectionUtils.isEmpty(productDetails)) {
      for (ProductDetail productDetail : productDetails) {
        productDetail.setPromotion(null);
      }

      productDetailRepository.saveAll(productDetails);
      log.info("Removed promotion {} from {} product details", promotionId, productDetails.size());
    }
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    log.info("Deleting promotion with ID: {}", id);

    Promotion promotion = findPromotionById(id);

    removePromotionFromAllProductDetails(id);

    promotion.setStatus(Status.DELETED);
    promotionRepository.save(promotion);

    log.info("Deleted promotion successfully with ID: {}", id);
  }

  private void validateProductDetailIds(List<Integer> productDetailIds) {
    if (CollectionUtils.isEmpty(productDetailIds)) {
      return;
    }

    List<ProductDetail> existingProductDetails =
        productDetailRepository.findAllById(productDetailIds);

    if (existingProductDetails.size() != productDetailIds.size()) {
      Map<String, String> errors = new HashMap<>();
      errors.put("productDetailIds", "Một số product detail không tồn tại");
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }

    for (ProductDetail pd : existingProductDetails) {
      if (Status.DELETED.equals(pd.getStatus())) {
        Map<String, String> errors = new HashMap<>();
        errors.put("productDetailIds", "ProductDetail " + pd.getId() + " đã bị xóa");
        throw new ValidationException("Dữ liệu không hợp lệ", errors);
      }
    }
  }

  @Override
  public PromotionResponseDTO getPromotionById(Integer id) {
    log.debug("Getting promotion by ID: {}", id);
    Promotion promotion = findPromotionById(id);
    return promotionMapper.toDto(promotion);
  }

  @Override
  public PromotionResponseDTO getPromotionByCode(String code) {
    log.debug("Getting promotion by code: {}", code);

    if (code == null || code.isEmpty()) {
      throw new IllegalArgumentException("Mã khuyến mãi không được để trống");
    }

    Promotion promotion =
        promotionRepository
            .findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", "code", code));

    return promotionMapper.toDto(promotion);
  }

  @Override
  public List<PromotionResponseDTO> getAllPromotions() {
    log.debug("Getting all promotions");
    return promotionMapper.toDtoList(promotionRepository.findAll());
  }

  @Override
  public Page<PromotionResponseDTO> getAllPaginated(Pageable pageable) {
    log.debug("Getting all promotions with pagination: {}", pageable);
    return promotionRepository.findAll(pageable).map(promotionMapper::toDto);
  }

  @Override
  public List<PromotionResponseDTO> getAllByStatus(Status status) {
    log.debug("Getting all promotions with status: {}", status);
    return promotionMapper.toDtoList(
        promotionRepository.findAll().stream()
            .filter(promotion -> promotion.getStatus() == status)
            .toList());
  }

  @Override
  public Page<PromotionResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    log.debug("Getting promotions with status {} and pagination: {}", status, pageable);

    Page<Promotion> promotionPage =
        promotionRepository.findPromotionsWithFilters(null, null, status, null, null, pageable);
    return promotionPage.map(promotionMapper::toDto);
  }

  @Override
  public Page<PromotionResponseDTO> searchPromotions(PromotionSearchDTO searchRequest) {
    log.debug("Searching promotions with filters: {}", searchRequest);

    Sort sort =
        searchRequest.getSortDir().equalsIgnoreCase("desc")
            ? Sort.by(searchRequest.getSortBy()).descending()
            : Sort.by(searchRequest.getSortBy()).ascending();

    Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);

    Status status = null;
    if (searchRequest.getStatus() != null) {
      switch (searchRequest.getStatus()) {
        case 1:
          status = Status.ACTIVE;
          break;
        case 0:
          status = Status.INACTIVE;
          break;
        case 14:
          status = Status.PENDING_START;
          break;
        default:
          status = Status.INACTIVE;
      }
    }

    Page<Promotion> promotionPage =
        promotionRepository.findPromotionsWithFilters(
            searchRequest.getName(),
            searchRequest.getCode(),
            status,
            searchRequest.getDiscountType(),
            searchRequest.getAppliesTo(),
            pageable);

    return promotionPage.map(promotionMapper::toDto);
  }

  @Override
  public List<PromotionResponseDTO> getActivePromotions() {
    log.debug("Getting active promotions");
    List<Promotion> promotions =
        promotionRepository.findActivePromotions(LocalDateTime.now(), Status.ACTIVE);
    return promotionMapper.toDtoList(promotions);
  }

  @Override
  @Transactional
  public PromotionResponseDTO toggleStatus(Integer id) {
    log.info("Toggling status for promotion with ID: {}", id);

    Promotion promotion = findPromotionById(id);
    Status currentStatus = promotion.getStatus();
    Status newStatus;

    switch (currentStatus) {
      case ACTIVE:
        newStatus = Status.INACTIVE;
        break;
      case INACTIVE:
      case PENDING_START:
        if (canPromotionBeActivated(promotion)) {
          if (promotion.getStartDate() != null
              && promotion.getStartDate().isAfter(LocalDateTime.now())) {
            newStatus = Status.PENDING_START;
          } else {
            newStatus = Status.ACTIVE;
          }
        } else {
          newStatus = Status.INACTIVE;
        }
        break;
      default:
        throw new ValidationException(
            "Không thể thay đổi trạng thái của promotion đã bị xóa", null);
    }

    promotion.setStatus(newStatus);
    Promotion updatedPromotion = promotionRepository.save(promotion);

    log.info("Toggled status for promotion ID: {} from {} to {}", id, currentStatus, newStatus);
    return promotionMapper.toDto(updatedPromotion);
  }

  @Transactional
  @Override
  public void incrementUsageCount(Integer id) {
    log.info("Incrementing used count for promotion with ID: {}", id);

    Promotion promotion = findPromotionById(id);
    promotionRepository.save(promotion);

    log.info("Incremented used count for promotion ID: {}", id);
  }

  @Override
  public boolean canUsePromotion(Integer id, BigDecimal productPrice) {
    log.debug("Checking if promotion {} can be used for product price: {}", id, productPrice);

    try {
      Promotion promotion = findPromotionById(id);
      return isPromotionUsable(promotion, productPrice);
    } catch (Exception e) {
      log.error("Error checking promotion usage: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public PromotionDiscountResponseDTO calculateDiscountAmount(Integer id, BigDecimal productPrice) {
    log.debug(
        "Calculating discount amount for promotion {} with product price: {}", id, productPrice);

    try {
      Promotion promotion = findPromotionById(id);

      if (!isPromotionUsable(promotion, productPrice)) {
        return promotionMapper.toDiscountResponse(
            promotion, BigDecimal.ZERO, productPrice, false, "Khuyến mãi không thể sử dụng");
      }

      BigDecimal discountAmount = calculateDiscount(promotion, productPrice);
      BigDecimal finalAmount = productPrice.subtract(discountAmount);

      return promotionMapper.toDiscountResponse(
          promotion, discountAmount, finalAmount, true, "Áp dụng khuyến mãi thành công");

    } catch (ResourceNotFoundException e) {
      log.error("Promotion not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error calculating discount: {}", e.getMessage());
      throw new RuntimeException("Lỗi tính toán giảm giá: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public PromotionDiscountResponseDTO applyPromotion(Integer id, BigDecimal productPrice) {
    log.info("Applying promotion {} for product price: {}", id, productPrice);

    PromotionDiscountResponseDTO response = calculateDiscountAmount(id, productPrice);

    if (response.getCanUse()) {
      incrementUsageCount(id);
      log.info("Applied promotion {} successfully", id);
    }

    return response;
  }

  @Override
  public List<PromotionResponseDTO> getPromotionsForProduct(String productId) {
    log.debug("Getting promotions for product: {}", productId);

    LocalDateTime now = LocalDateTime.now();
    List<Promotion> productPromotions =
        promotionRepository.findActivePromotionsForProduct(
            now, Status.ACTIVE, "product", productId);
    List<Promotion> allPromotions =
        promotionRepository.findActivePromotionsByAppliesTo(now, Status.ACTIVE, "all");

    productPromotions.addAll(allPromotions);

    return promotionMapper.toDtoList(productPromotions);
  }

  @Override
  public List<PromotionResponseDTO> getPromotionsForCategory(String categoryId) {
    log.debug("Getting promotions for category: {}", categoryId);

    LocalDateTime now = LocalDateTime.now();
    List<Promotion> categoryPromotions =
        promotionRepository.findActivePromotionsForProduct(
            now, Status.ACTIVE, "category", categoryId);
    List<Promotion> allPromotions =
        promotionRepository.findActivePromotionsByAppliesTo(now, Status.ACTIVE, "all");

    categoryPromotions.addAll(allPromotions);

    return promotionMapper.toDtoList(categoryPromotions);
  }

  @Override
  public boolean existsByCode(String code) {
    return promotionRepository.existsByCode(code);
  }

  private Promotion findPromotionById(Integer id) {
    return promotionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
  }

  private Status determineInitialStatus(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null || endDate == null) {
      return Status.INACTIVE;
    }

    LocalDateTime now = LocalDateTime.now();

    if (startDate.isAfter(endDate)) {
      return Status.INACTIVE;
    }

    if (now.isBefore(startDate)) {
      return Status.PENDING_START;
    } else if (now.isAfter(endDate)) {
      return Status.INACTIVE;
    } else {
      return Status.ACTIVE;
    }
  }

  private Status determinePromotionStatusForUpdate(
      Promotion promotion, LocalDateTime now, Status oldStatus) {
    LocalDateTime startDate = promotion.getStartDate();
    LocalDateTime endDate = promotion.getEndDate();

    if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
      return Status.INACTIVE;
    }

    if (Status.DELETED.equals(oldStatus)) {
      return Status.DELETED;
    }

    if (Status.INACTIVE.equals(oldStatus) && now.isAfter(startDate) && now.isBefore(endDate)) {
      return Status.INACTIVE;
    }

    if (now.isBefore(startDate)) {
      return Status.PENDING_START;
    } else if (now.isAfter(endDate)) {
      return Status.INACTIVE;
    } else {
      if (Status.PENDING_START.equals(oldStatus)) {
        return Status.ACTIVE;
      }
      return oldStatus;
    }
  }

  private boolean isPromotionUsable(Promotion promotion, BigDecimal productPrice) {
    LocalDateTime now = LocalDateTime.now();

    boolean isActive = promotion.getStatus() == Status.ACTIVE;
    boolean isNotExpired =
        now.isAfter(promotion.getStartDate()) && now.isBefore(promotion.getEndDate());

    if (Status.PENDING_START.equals(promotion.getStatus())) {
      return false;
    }

    return isActive && isNotExpired;
  }

  private BigDecimal calculateDiscount(Promotion promotion, BigDecimal productPrice) {
    BigDecimal discountAmount;

    if ("percentage".equals(promotion.getDiscountType())) {
      discountAmount =
          productPrice
              .multiply(promotion.getDiscountValue())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    } else {
      discountAmount = promotion.getDiscountValue();
    }

    if (discountAmount.compareTo(productPrice) > 0) {
      discountAmount = productPrice;
    }

    return discountAmount;
  }

  @Override
  public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Promotion promotion) {
    if (promotion == null || originalPrice == null) {
      return originalPrice;
    }

    LocalDateTime now = LocalDateTime.now();
    if (promotion.getStartDate().isAfter(now)
        || promotion.getEndDate().isBefore(now)
        || !Status.ACTIVE.equals(promotion.getStatus())) {
      return originalPrice;
    }

    BigDecimal discountAmount;

    if ("percentage".equals(promotion.getDiscountType())) {
      discountAmount =
          originalPrice
              .multiply(promotion.getDiscountValue())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    } else if ("fixed_amount".equals(promotion.getDiscountType())) {
      discountAmount = promotion.getDiscountValue();
    } else {
      return originalPrice;
    }

    BigDecimal finalPrice = originalPrice.subtract(discountAmount);
    return finalPrice.max(BigDecimal.ZERO);
  }

  @Override
  public PromotionResponseDTO getPromotionByIdWithDetails(Integer id) {
    log.debug("Getting promotion with details by ID: {}", id);

    Promotion promotion = findPromotionById(id);

    List<Product> products = promotionRepository.findProductsByPromotionId(id);

    PromotionResponseDTO responseDTO = promotionMapper.toDto(promotion);

    if (products != null && !products.isEmpty()) {
      List<ProductResponseDTO> productDTOs = products.stream().map(productMapper::toDto).toList();
      responseDTO.setProducts(productDTOs);
    }

    List<ProductDetail> productDetails = promotionRepository.findByPromotionId(id);
    if (productDetails != null && !productDetails.isEmpty()) {
      List<ProductDetailResponseDTO> productDetailDTOs =
          productDetails.stream().map(productDetailMapper::toDto).toList();
      responseDTO.setProductDetails(productDetailDTOs);
    }

    log.info(
        "Found {} products and {} product details using promotion ID: {}",
        products != null ? products.size() : 0,
        productDetails != null ? productDetails.size() : 0,
        id);

    return responseDTO;
  }

  private boolean canPromotionBeActivated(Promotion promotion) {
    LocalDateTime now = LocalDateTime.now();

    if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
      return false;
    }

    if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
      return false;
    }

    if (now.isAfter(promotion.getEndDate())) {
      return false;
    }

    return true;
  }

  private Map<String, String> validatePromotionData(Object dto, Promotion existingPromotion) {
    Map<String, String> errors = new HashMap<>();

    String code = null;
    LocalDateTime startDate = null;
    LocalDateTime endDate = null;
    BigDecimal discountValue = null;

    if (dto instanceof PromotionCreateDTO) {
      PromotionCreateDTO createDto = (PromotionCreateDTO) dto;
      code = createDto.getCode();
      startDate = createDto.getStartDate();
      endDate = createDto.getEndDate();
      discountValue = createDto.getDiscountValue();
    } else if (dto instanceof PromotionUpdateDTO) {
      PromotionUpdateDTO updateDto = (PromotionUpdateDTO) dto;
      if (updateDto.getStartDate() != null) startDate = updateDto.getStartDate();
      if (updateDto.getEndDate() != null) endDate = updateDto.getEndDate();
      if (updateDto.getDiscountValue() != null) discountValue = updateDto.getDiscountValue();
    }

    if (code != null && !code.isEmpty()) {
      boolean codeExists =
          existingPromotion != null
              ? promotionRepository.existsByCodeAndIdNot(code, existingPromotion.getId())
              : promotionRepository.existsByCode(code);

      if (codeExists) {
        errors.put("code", "Mã khuyến mãi đã tồn tại: " + code);
      }
    }

    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      errors.put("endDate", "Ngày kết thúc phải sau ngày bắt đầu");
    }

    if (discountValue != null && discountValue.compareTo(BigDecimal.ZERO) <= 0) {
      errors.put("discountValue", "Giá trị giảm giá phải lớn hơn 0");
    }

    return errors;
  }
}
