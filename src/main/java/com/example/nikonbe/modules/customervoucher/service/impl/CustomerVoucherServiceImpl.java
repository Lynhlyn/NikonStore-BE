package com.example.nikonbe.modules.customervoucher.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.customervoucher.dto.request.CustomerVoucherAssignDTO;
import com.example.nikonbe.modules.customervoucher.dto.response.CustomerVoucherResponseDTO;
import com.example.nikonbe.modules.customervoucher.entity.CustomerVoucher;
import com.example.nikonbe.modules.customervoucher.mapper.CustomerVoucherMapper;
import com.example.nikonbe.modules.customervoucher.repository.CustomerVoucherRepository;
import com.example.nikonbe.modules.customervoucher.service.interF.CustomerVoucherService;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import com.example.nikonbe.modules.voucher.repository.VoucherRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class CustomerVoucherServiceImpl implements CustomerVoucherService {

  private final CustomerVoucherRepository customerVoucherRepository;
  private final CustomerRepository customerRepository;
  private final VoucherRepository voucherRepository;
  private final CustomerVoucherMapper customerVoucherMapper;

  @Override
  @Transactional
  public List<CustomerVoucherResponseDTO> assignVouchersToCustomer(CustomerVoucherAssignDTO dto) {
    log.info("Assigning vouchers to customer with ID: {}", dto.getCustomerId());

    Customer customer =
        customerRepository
            .findById(dto.getCustomerId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));

    List<CustomerVoucher> assignedVouchers = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    for (Long voucherId : dto.getVoucherIds()) {
      try {
        Voucher voucher =
            voucherRepository
                .findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", voucherId));

        if (customerVoucherRepository.existsByIdCustomerIdAndIdVoucherId(
            customer.getId(), voucher.getId())) {
          errors.add("Voucher với ID " + voucherId + " đã được gán cho khách hàng.");
          continue;
        }

        CustomerVoucher customerVoucher = customerVoucherMapper.createEntity(customer, voucher);
        assignedVouchers.add(customerVoucherRepository.save(customerVoucher));
        log.info("Assigned voucher ID {} to customer ID {}", voucherId, dto.getCustomerId());
      } catch (Exception e) {
        errors.add("Lỗi gán voucher với ID " + voucherId + ": " + e.getMessage());
        log.error(
            "Error assigning voucher with ID {} to customer {}: {}",
            voucherId,
            dto.getCustomerId(),
            e.getMessage());
      }
    }

    if (!errors.isEmpty() && assignedVouchers.isEmpty()) {
      throw new ValidationException("Không thể gán voucher", Map.of("errors", errors.toString()));
    }

    return customerVoucherMapper.toDtoList(assignedVouchers);
  }

  @Override
  @Transactional
  public void assignVoucherToCustomers(Long voucherId, List<Integer> customerIds) {
    log.info("Assigning voucher ID {} to multiple customers", voucherId);

    Voucher voucher =
        voucherRepository
            .findById(voucherId)
            .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", voucherId));

    List<String> errors = new ArrayList<>();

    for (Integer customerId : customerIds) {
      try {
        Customer customer =
            customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (customerVoucherRepository.existsByIdCustomerIdAndIdVoucherId(
            customer.getId(), voucher.getId())) {
          errors.add("Voucher đã được gán cho khách hàng ID " + customerId);
          continue;
        }

        CustomerVoucher customerVoucher = customerVoucherMapper.createEntity(customer, voucher);
        customerVoucherRepository.save(customerVoucher);
        log.info("Assigned voucher ID {} to customer ID {}", voucherId, customerId);
      } catch (Exception e) {
        errors.add("Lỗi gán cho khách hàng ID " + customerId + ": " + e.getMessage());
        log.error(
            "Error assigning voucher ID {} to customer ID {}: {}",
            voucherId,
            customerId,
            e.getMessage());
      }
    }

    if (!errors.isEmpty()) {
      if (errors.size() == customerIds.size()) {
        throw new ValidationException(
            "Không thể gán voucher cho bất kỳ khách hàng nào", Map.of("errors", errors.toString()));
      }
      log.warn("Some customers were not assigned voucher ID {}: {}", voucherId, errors);
    }
  }

  @Override
  public List<CustomerVoucherResponseDTO> getVouchersByCustomerId(Integer customerId) {
    log.debug("Getting vouchers for customer ID: {}", customerId);

    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }

    List<CustomerVoucher> customerVouchers = customerVoucherRepository.findByCustomerId(customerId);
    return customerVoucherMapper.toDtoList(customerVouchers);
  }

  @Override
  public Page<CustomerVoucherResponseDTO> getVouchersByCustomerIdPaginated(
      Integer customerId, Pageable pageable) {
    log.debug("Getting paginated vouchers for customer ID: {}", customerId);

    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }

    Page<CustomerVoucher> customerVoucherPage =
        customerVoucherRepository.findByCustomerId(customerId, pageable);
    return customerVoucherPage.map(customerVoucherMapper::toDto);
  }

  @Override
  public List<CustomerVoucherResponseDTO> getUsedVouchersByCustomerId(Integer customerId) {
    log.debug("Getting used vouchers for customer ID: {}", customerId);

    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }

    List<CustomerVoucher> usedVouchers =
        customerVoucherRepository.findUsedVouchersByCustomerId(customerId);
    return customerVoucherMapper.toDtoList(usedVouchers);
  }

  @Override
  public List<CustomerVoucherResponseDTO> getUnusedVouchersByCustomerId(Integer customerId) {
    log.debug("Getting unused vouchers for customer ID: {}", customerId);

    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }

    List<CustomerVoucher> unusedVouchers =
        customerVoucherRepository.findUnusedVouchersByCustomerId(customerId);
    return customerVoucherMapper.toDtoList(unusedVouchers);
  }

  @Override
  public boolean customerHasVoucher(Integer customerId, Long voucherId) {
    log.debug("Checking if customer ID {} has voucher ID {}", customerId, voucherId);
    return customerVoucherRepository.existsByIdCustomerIdAndIdVoucherId(customerId, voucherId);
  }

  @Override
  @Transactional
  public CustomerVoucherResponseDTO useVoucher(Integer customerId, Long voucherId) {
    log.info("Using voucher ID {} for customer ID {}", voucherId, customerId);

    CustomerVoucher customerVoucher =
        customerVoucherRepository
            .findByIdCustomerIdAndIdVoucherId(customerId, voucherId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "CustomerVoucher",
                        "customerId and voucherId",
                        customerId + ", " + voucherId));

    if (customerVoucher.getUsedAt() != null) {
      Map<String, String> errors = new HashMap<>();
      errors.put("usedAt", "Voucher đã được sử dụng lúc " + customerVoucher.getUsedAt());
      throw new ValidationException("Voucher already used", errors);
    }

    customerVoucher.setUsedAt(LocalDateTime.now());
    CustomerVoucher savedCustomerVoucher = customerVoucherRepository.save(customerVoucher);
    log.info("Marked voucher ID {} as used by customer ID {}", voucherId, customerId);

    return customerVoucherMapper.toDto(savedCustomerVoucher);
  }

  @Override
  @Transactional
  public void removeVoucherFromCustomer(Integer customerId, Long voucherId) {
    log.info("Removing voucher ID {} from customer ID {}", voucherId, customerId);

    CustomerVoucher customerVoucher =
        customerVoucherRepository
            .findByIdCustomerIdAndIdVoucherId(customerId, voucherId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "CustomerVoucher",
                        "customerId and voucherId",
                        customerId + ", " + voucherId));

    if (customerVoucher.getUsedAt() != null) {
      Map<String, String> errors = new HashMap<>();
      errors.put(
          "usedAt",
          "Không thể gỡ khách hàng khỏi voucher đã được sử dụng lúc "
              + customerVoucher.getUsedAt());
      throw new ValidationException("Cannot remove customer from used voucher", errors);
    }

    customerVoucherRepository.deleteById(customerVoucher.getId());
    log.info("Removed voucher ID {} from customer ID {}", voucherId, customerId);
  }

  @Override
  public long countVouchersByCustomerId(Integer customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }

    return customerVoucherRepository.countByCustomerId(customerId);
  }

  @Override
  public long countUsedVouchersByCustomerId(Integer customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }

    return customerVoucherRepository.countUsedVouchersByCustomerId(customerId);
  }

  @Override
  public Page<CustomerVoucherResponseDTO> getCustomersByVoucherIdPaginated(
      Long voucherId, Pageable pageable) {
    log.debug("Getting customers for voucher ID: {} with pagination", voucherId);

    if (!voucherRepository.existsById(voucherId)) {
      throw new ResourceNotFoundException("Voucher", "id", voucherId);
    }

    Page<CustomerVoucher> customerVoucherPage =
        customerVoucherRepository.findCustomersByVoucherId(voucherId, pageable);
    return customerVoucherPage.map(customerVoucherMapper::toDto);
  }
}
