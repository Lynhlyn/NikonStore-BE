package com.example.nikonbe.modules.voucher.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.modules.voucher.dto.request.VoucherCreateDTO;
import com.example.nikonbe.modules.voucher.dto.request.VoucherUpdateDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherDiscountResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherWithCustomersResponseDTO;
import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {

  VoucherResponseDTO create(VoucherCreateDTO dto);

  VoucherResponseDTO update(Long id, VoucherUpdateDTO dto);

  VoucherResponseDTO getById(Long id);

  VoucherWithCustomersResponseDTO getByIdWithCustomers(Long id);

  VoucherResponseDTO getByCode(String code, Integer customerId);

  ApiResponseDto<List<VoucherResponseDTO>> getAllVouchers(
      String code,
      Status status,
      String discountType,
      Boolean isPublic,
      boolean isAll,
      String sortBy,
      String sortDir,
      int page,
      int size,
      Integer customerId);

  List<VoucherResponseDTO> getPublicActiveVouchers();

  List<VoucherResponseDTO> getAvailableVouchersForCustomer(Integer customerId);

  void delete(Long id);

  VoucherResponseDTO toggleStatus(Long id);

  VoucherDiscountResponseDTO applyVoucher(String code, Integer customerId, BigDecimal orderValue);

  boolean existsByCode(String code);
}
