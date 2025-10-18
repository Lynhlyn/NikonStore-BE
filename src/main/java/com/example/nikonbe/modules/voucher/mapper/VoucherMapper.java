package com.example.nikonbe.modules.voucher.mapper;

import com.example.nikonbe.modules.voucher.dto.request.VoucherCreateDTO;
import com.example.nikonbe.modules.voucher.dto.request.VoucherUpdateDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherDiscountResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VoucherMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "ACTIVE")
  @Mapping(target = "usedCount", constant = "0")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Voucher toEntity(VoucherCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDto(VoucherUpdateDTO dto, @MappingTarget Voucher entity);

  VoucherResponseDTO toDto(Voucher entity);

  List<VoucherResponseDTO> toDtoList(List<Voucher> entities);

  @Named("toDiscountResponse")
  default VoucherDiscountResponseDTO toDiscountResponse(
      Voucher voucher,
      BigDecimal discountAmount,
      BigDecimal orderValue,
      boolean canUse,
      String message) {
    if (voucher == null) {
      return null;
    }

    return VoucherDiscountResponseDTO.builder()
        .code(voucher.getCode())
        .discountAmount(discountAmount)
        .finalAmount(orderValue.subtract(discountAmount))
        .canUse(canUse)
        .message(message)
        .build();
  }
}
