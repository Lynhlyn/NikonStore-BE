package com.example.nikonbe.modules.promotion.mapper;

import com.example.nikonbe.modules.promotion.dto.request.PromotionCreateDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionUpdateDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionDiscountResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.promotion.entity.Promotion;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromotionMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "appliesTo", constant = "product")
  @Mapping(target = "appliedProduct", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Promotion toEntity(PromotionCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "appliesTo", ignore = true)
  @Mapping(target = "appliedProduct", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromDto(PromotionUpdateDTO dto, @MappingTarget Promotion entity);

  @Mapping(target = "products", ignore = true)
  @Mapping(target = "productDetails", ignore = true)
  PromotionResponseDTO toDto(Promotion entity);

  List<PromotionResponseDTO> toDtoList(List<Promotion> entities);

  default PromotionDiscountResponseDTO toDiscountResponse(
      Promotion promotion,
      BigDecimal discountAmount,
      BigDecimal finalAmount,
      boolean canUse,
      String message) {
    if (promotion == null) {
      return null;
    }

    return PromotionDiscountResponseDTO.builder()
        .promotionId(promotion.getId())
        .name(promotion.getName())
        .code(promotion.getCode())
        .discountAmount(discountAmount)
        .finalAmount(finalAmount)
        .canUse(canUse)
        .message(message)
        .build();
  }
}
