package com.example.nikonbe.modules.voucher.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.voucher.dto.request.VoucherCreateDTO;
import com.example.nikonbe.modules.voucher.dto.request.VoucherUpdateDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherDiscountResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import com.example.nikonbe.modules.voucher.mapper.VoucherMapper;
import com.example.nikonbe.modules.voucher.repository.VoucherRepository;
import com.example.nikonbe.modules.voucher.service.interF.VoucherService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherServiceImpl implements VoucherService {

  private final VoucherRepository voucherRepository;
  private final VoucherMapper voucherMapper;

  @Override
  @Transactional
  public VoucherResponseDTO create(VoucherCreateDTO dto) {
    log.info("Creating new voucher with code: {}", dto.getCode());

    validateVoucherData(dto, null);

    Voucher voucher = voucherMapper.toEntity(dto);
    if (dto.getIsPublic() != null) {
      voucher.setIsPublic(dto.getIsPublic());
    }

    Status initialStatus =
        determineInitialStatus(
            voucher.getStartDate(),
            voucher.getEndDate(),
            voucher.getQuantity(),
            voucher.getUsedCount());
    voucher.setStatus(initialStatus);

    Voucher savedVoucher = voucherRepository.save(voucher);

    log.info(
        "Created voucher successfully with ID: {} and status: {}",
        savedVoucher.getId(),
        savedVoucher.getStatus());
    return voucherMapper.toDto(savedVoucher);
  }

  @Override
  @Transactional
  public VoucherResponseDTO update(Long id, VoucherUpdateDTO dto) {
    log.info("Updating voucher with ID: {}", id);

    Voucher voucher = findVoucherById(id);
    validateVoucherData(dto, voucher);

    Status oldStatus = voucher.getStatus();
    voucherMapper.updateEntityFromDto(dto, voucher);
    if (dto.getIsPublic() != null) {
      voucher.setIsPublic(dto.getIsPublic());
    }

    if (dto.getStatus() != null) {
      voucher.setStatus(dto.getStatus());
      log.info("Admin manually set voucher status to {} for ID: {}", dto.getStatus(), id);
    } else {
      if (!Status.DELETED.equals(oldStatus)) {
        LocalDateTime startDate = voucher.getStartDate();
        LocalDateTime endDate = voucher.getEndDate();
        Integer quantity = voucher.getQuantity();
        Integer usedCount = voucher.getUsedCount();

        if (startDate != null && endDate != null) {
          Status newStatus =
              determineVoucherStatusForUpdate(
                  voucher, LocalDateTime.now(), oldStatus, quantity, usedCount);
          voucher.setStatus(newStatus);
          log.info(
              "Auto-updated voucher status from {} to {} for ID: {}", oldStatus, newStatus, id);
        }
      }
    }

    Voucher updatedVoucher = voucherRepository.save(voucher);
    log.info("Updated voucher successfully with ID: {}", updatedVoucher.getId());
    return voucherMapper.toDto(updatedVoucher);
  }

  @Override
  public VoucherResponseDTO getById(Long id) {
    Voucher voucher = findVoucherById(id);
    return voucherMapper.toDto(voucher);
  }

  @Override
  public VoucherResponseDTO getByCode(String code) {
    Voucher voucher = findVoucherByCode(code);
    return voucherMapper.toDto(voucher);
  }

  @Override
  public Page<VoucherResponseDTO> getAllVouchers(
      String code, Status status, String discountType, Boolean isPublic, Pageable pageable) {

    log.info(
        "Getting vouchers with filters - code: {}, status: {}, discountType: {}, isPublic: {}",
        code,
        status,
        discountType,
        isPublic);

    Page<Voucher> voucherPage =
        voucherRepository.findVouchersWithFilters(code, status, discountType, isPublic, pageable);
    return voucherPage.map(voucherMapper::toDto);
  }

  @Override
  public List<VoucherResponseDTO> getPublicActiveVouchers() {
    List<Voucher> vouchers = voucherRepository.findPublicActiveVouchers(LocalDateTime.now());
    return voucherMapper.toDtoList(vouchers);
  }

  @Override
  public List<VoucherResponseDTO> getAvailableVouchersForCustomer(Integer customerId) {
    if (customerId == null) {
      log.warn("CustomerId is null, returning only public vouchers");
      return getPublicActiveVouchers();
    }

    log.info("Getting available vouchers for customer ID: {}", customerId);
    List<Voucher> publicVouchers = voucherRepository.findPublicActiveVouchers(LocalDateTime.now());

    List<Voucher> availableVouchers =
        publicVouchers.stream().filter(this::isVoucherAvailable).toList();

    log.info("Found {} available vouchers for customer {}", availableVouchers.size(), customerId);
    return voucherMapper.toDtoList(availableVouchers);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting voucher with ID: {}", id);
    Voucher voucher = findVoucherById(id);
    voucher.setStatus(Status.DELETED);
    voucherRepository.save(voucher);
    log.info("Deleted voucher successfully with ID: {}", id);
  }

  @Override
  @Transactional
  public VoucherResponseDTO toggleStatus(Long id) {
    log.info("Toggling status for voucher with ID: {}", id);
    Voucher voucher = findVoucherById(id);

    Status newStatus;
    if (voucher.getStatus() == Status.ACTIVE) {
      newStatus = Status.INACTIVE;
    } else if (voucher.getStatus() == Status.INACTIVE || voucher.getStatus() == Status.PENDING) {
      if (canVoucherBeActivated(voucher)) {
        if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDateTime.now())) {
          newStatus = Status.PENDING;
        } else {
          newStatus = Status.ACTIVE;
        }
      } else {
        newStatus = Status.INACTIVE;
      }
    } else {
      throw new ValidationException("Không thể thay đổi trạng thái của voucher đã bị xóa", null);
    }

    voucher.setStatus(newStatus);
    Voucher updatedVoucher = voucherRepository.save(voucher);
    log.info("Toggled voucher status to: {} for ID: {}", newStatus, id);
    return voucherMapper.toDto(updatedVoucher);
  }

  @Override
  @Transactional
  public VoucherDiscountResponseDTO applyVoucher(
      String code, Integer customerId, BigDecimal orderValue) {
    Voucher voucher = findVoucherByCode(code);

    if (!isVoucherUsable(voucher, orderValue)) {
      return voucherMapper.toDiscountResponse(
          voucher, BigDecimal.ZERO, orderValue, false, "Voucher không thể sử dụng");
    }

    BigDecimal discountAmount = calculateDiscount(voucher, orderValue);
    voucher.setUsedCount(voucher.getUsedCount() + 1);
    voucherRepository.save(voucher);

    return voucherMapper.toDiscountResponse(
        voucher, discountAmount, orderValue, true, "Áp dụng voucher thành công");
  }

  @Override
  public boolean existsByCode(String code) {
    return voucherRepository.existsByCode(code);
  }

  private Voucher findVoucherById(Long id) {
    return voucherRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", id));
  }

  private Voucher findVoucherByCode(String code) {
    return voucherRepository
        .findByCode(code)
        .orElseThrow(() -> new ResourceNotFoundException("Voucher", "code", code));
  }

  private void validateVoucherData(Object dto, Voucher existingVoucher) {
    Map<String, String> errors = new HashMap<>();

    String code = null;
    LocalDateTime startDate = null;
    LocalDateTime endDate = null;
    BigDecimal discountValue = null;
    BigDecimal minOrderValue = null;
    Integer quantity = null;

    if (dto instanceof VoucherCreateDTO) {
      VoucherCreateDTO createDto = (VoucherCreateDTO) dto;
      code = createDto.getCode();
      startDate = createDto.getStartDate();
      endDate = createDto.getEndDate();
      discountValue = createDto.getDiscountValue();
      minOrderValue = createDto.getMinOrderValue();
      quantity = createDto.getQuantity();
    } else if (dto instanceof VoucherUpdateDTO) {
      VoucherUpdateDTO updateDto = (VoucherUpdateDTO) dto;
      if (updateDto.getStartDate() != null) startDate = updateDto.getStartDate();
      if (updateDto.getEndDate() != null) endDate = updateDto.getEndDate();
      if (updateDto.getDiscountValue() != null) discountValue = updateDto.getDiscountValue();
      if (updateDto.getMinOrderValue() != null) minOrderValue = updateDto.getMinOrderValue();
      if (updateDto.getQuantity() != null) quantity = updateDto.getQuantity();
    }

    if (code != null) {
      boolean codeExists =
          existingVoucher != null
              ? voucherRepository.existsByCodeAndIdNot(code, existingVoucher.getId())
              : voucherRepository.existsByCode(code);

      if (codeExists) {
        errors.put("code", "Mã voucher đã tồn tại: " + code);
      }
    }

    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      errors.put("endDate", "Ngày kết thúc phải sau ngày bắt đầu");
    }

    if (discountValue != null && discountValue.compareTo(BigDecimal.ZERO) <= 0) {
      errors.put("discountValue", "Giá trị giảm giá phải lớn hơn 0");
    }

    if (minOrderValue != null && minOrderValue.compareTo(BigDecimal.ZERO) < 0) {
      errors.put("minOrderValue", "Giá trị đơn hàng tối thiểu không được âm");
    }

    if (quantity != null && quantity < 0) {
      errors.put("quantity", "Số lượng không được âm");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }
  }

  private boolean isVoucherUsable(Voucher voucher, BigDecimal orderValue) {
    LocalDateTime now = LocalDateTime.now();
    return voucher.getStatus() == Status.ACTIVE
        && now.isAfter(voucher.getStartDate())
        && now.isBefore(voucher.getEndDate())
        && voucher.getQuantity() > 0
        && orderValue.compareTo(voucher.getMinOrderValue()) >= 0;
  }

  private boolean isVoucherAvailable(Voucher voucher) {
    LocalDateTime now = LocalDateTime.now();
    return voucher.getStatus() == Status.ACTIVE
        && now.isAfter(voucher.getStartDate())
        && now.isBefore(voucher.getEndDate())
        && voucher.getQuantity() > 0;
  }

  private BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderValue) {
    BigDecimal discountAmount;
    if ("percentage".equals(voucher.getDiscountType())) {
      discountAmount =
          orderValue
              .multiply(voucher.getDiscountValue())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      if (voucher.getMaxDiscount() != null
          && discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
        discountAmount = voucher.getMaxDiscount();
      }
    } else {
      discountAmount = voucher.getDiscountValue();
    }
    return discountAmount.compareTo(orderValue) > 0 ? orderValue : discountAmount;
  }

  private Status determineInitialStatus(
      LocalDateTime startDate, LocalDateTime endDate, Integer quantity, Integer usedCount) {

    if (startDate == null || endDate == null) {
      return Status.INACTIVE;
    }

    LocalDateTime now = LocalDateTime.now();

    if (startDate.isAfter(endDate)) {
      return Status.INACTIVE;
    }

    if (quantity <= 0 || (usedCount != null && usedCount >= quantity)) {
      return Status.INACTIVE;
    }

    if (now.isBefore(startDate)) {
      return Status.PENDING;
    } else if (now.isAfter(endDate)) {
      return Status.INACTIVE;
    } else {
      return Status.ACTIVE;
    }
  }

  private Status determineVoucherStatusForUpdate(
      Voucher voucher, LocalDateTime now, Status oldStatus, Integer quantity, Integer usedCount) {

    LocalDateTime startDate = voucher.getStartDate();
    LocalDateTime endDate = voucher.getEndDate();

    if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
      return Status.INACTIVE;
    }

    if (Status.DELETED.equals(oldStatus)) {
      return Status.DELETED;
    }

    if (quantity <= 0 || (usedCount != null && usedCount >= quantity)) {
      return Status.INACTIVE;
    }

    if (Status.INACTIVE.equals(oldStatus) && now.isAfter(startDate) && now.isBefore(endDate)) {
      return Status.INACTIVE;
    }

    if (now.isBefore(startDate)) {
      return Status.PENDING;
    } else if (now.isAfter(endDate)) {
      return Status.INACTIVE;
    } else {
      if (Status.PENDING.equals(oldStatus)) {
        return Status.ACTIVE;
      }
      return oldStatus;
    }
  }

  private boolean canVoucherBeActivated(Voucher voucher) {
    LocalDateTime now = LocalDateTime.now();

    if (voucher.getStartDate() == null || voucher.getEndDate() == null) {
      return false;
    }

    if (voucher.getStartDate().isAfter(voucher.getEndDate())) {
      return false;
    }

    if (voucher.getQuantity() <= 0) {
      return false;
    }

    if (now.isAfter(voucher.getEndDate())) {
      return false;
    }

    return true;
  }
}
