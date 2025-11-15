package com.example.nikonbe.modules.promotion.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.promotion.dto.request.PromotionCreateDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionSearchDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionUpdateDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionDiscountResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.promotion.entity.Promotion;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface PromotionService {
  PromotionResponseDTO createPromotion(PromotionCreateDTO request);

  PromotionResponseDTO updatePromotion(Integer id, PromotionUpdateDTO request);

  PromotionResponseDTO getPromotionById(Integer id);

  PromotionResponseDTO getPromotionByIdWithDetails(Integer id);

  void delete(Integer id);

  PromotionResponseDTO toggleStatus(Integer id);

  List<PromotionResponseDTO> getAllByStatus(Status status);

  Page<PromotionResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  Page<PromotionResponseDTO> searchPromotions(PromotionSearchDTO searchRequest);

  List<PromotionResponseDTO> getActivePromotions();

  List<PromotionResponseDTO> getAllPromotions();

  Page<PromotionResponseDTO> getAllPaginated(Pageable pageable);

  PromotionDiscountResponseDTO calculateDiscountAmount(Integer id, BigDecimal productPrice);

  PromotionDiscountResponseDTO applyPromotion(Integer id, BigDecimal productPrice);

  @Transactional
  void incrementUsageCount(Integer id);

  boolean canUsePromotion(Integer id, BigDecimal productPrice);

  List<PromotionResponseDTO> getPromotionsForProduct(String productId);

  List<PromotionResponseDTO> getPromotionsForCategory(String categoryId);

  boolean existsByCode(String code);

  PromotionResponseDTO getPromotionByCode(String code);

  BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Promotion promotion);

  void applyPromotionToProductDetails(Integer promotionId, List<Integer> productDetailIds);

  void removePromotionFromAllProductDetails(Integer promotionId);
}
