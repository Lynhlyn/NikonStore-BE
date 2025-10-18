package com.example.nikonbe.modules.voucher.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.voucher.dto.request.VoucherCreateDTO;
import com.example.nikonbe.modules.voucher.dto.request.VoucherUpdateDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherDiscountResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {

  VoucherResponseDTO create(VoucherCreateDTO dto);

  VoucherResponseDTO update(Long id, VoucherUpdateDTO dto);

  VoucherResponseDTO getById(Long id);

  VoucherResponseDTO getByCode(String code);

  Page<VoucherResponseDTO> getAllVouchers(
      String code, Status status, String discountType, Boolean isPublic, Pageable pageable);

  List<VoucherResponseDTO> getPublicActiveVouchers();

  List<VoucherResponseDTO> getAvailableVouchersForCustomer(Integer customerId);

  void delete(Long id);

  VoucherResponseDTO toggleStatus(Long id);

  VoucherDiscountResponseDTO applyVoucher(String code, Integer customerId, BigDecimal orderValue);

  boolean existsByCode(String code);
}
