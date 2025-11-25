package com.example.nikonbe.modules.shipping_address.service.impl;

import com.example.nikonbe.common.exceptions.BusinessException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.shipping_address.dto.request.CreateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.request.UpdateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.response.ShippingAddressResponseDto;
import com.example.nikonbe.modules.shipping_address.entity.ShippingAddress;
import com.example.nikonbe.modules.shipping_address.mapper.ShippingAddressMapper;
import com.example.nikonbe.modules.shipping_address.repository.ShippingAddressRepository;
import com.example.nikonbe.modules.shipping_address.service.ShippingAddressService;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShippingAddressServiceImpl implements ShippingAddressService {

  private final ShippingAddressRepository shippingAddressRepository;
  private final CustomerRepository customerRepository;
  private final ShippingAddressMapper shippingAddressMapper;

  private static final int MAX_ADDRESSES_PER_CUSTOMER = 10;

  @Override
  @Transactional
  public ShippingAddressResponseDto create(CreateShippingAddressDTO dto) {
    log.info("Creating new shipping address for customer: {}", dto.getCustomerId());

    validateCreateRequest(dto);
    validateAddressLimit(dto.getCustomerId());

    ShippingAddress shippingAddress = shippingAddressMapper.toEntity(dto);
    Customer customer = findCustomerById(dto.getCustomerId());
    shippingAddress.setCustomer(customer);

    if (dto.getIsDefault() != null && dto.getIsDefault()) {
      clearDefaultAddressForCustomer(dto.getCustomerId());
      shippingAddress.setIsDefault(true);
    } else {
      boolean isFirstAddress = isFirstAddress(dto.getCustomerId());
      shippingAddress.setIsDefault(isFirstAddress);
      if (isFirstAddress) {
        log.info("Setting first address as default for customer: {}", dto.getCustomerId());
      }
    }

    ShippingAddress savedAddress = shippingAddressRepository.save(shippingAddress);
    log.info("Successfully created shipping address with ID: {}", savedAddress.getId());

    return shippingAddressMapper.toDto(savedAddress);
  }

  @Override
  @Transactional
  public ShippingAddressResponseDto update(Integer id, UpdateShippingAddressDTO dto) {
    log.info("Updating shipping address with ID: {}", id);

    validateUpdateRequest(dto);
    ShippingAddress existingAddress = findAddressById(id);

    if (dto.getIsDefault() != null && dto.getIsDefault()) {
      clearDefaultAddressForCustomer(existingAddress.getCustomer().getId());
      existingAddress.setIsDefault(true);
    }

    shippingAddressMapper.updateEntityFromDto(dto, existingAddress);
    existingAddress.setUpdatedAt(LocalDateTime.now());

    ShippingAddress updatedAddress = shippingAddressRepository.save(existingAddress);
    log.info("Successfully updated shipping address with ID: {}", id);

    return shippingAddressMapper.toDto(updatedAddress);
  }

  @Override
  public ShippingAddressResponseDto getById(Integer id) {
    log.debug("Getting shipping address by ID: {}", id);
    ShippingAddress address = findAddressById(id);
    return shippingAddressMapper.toDto(address);
  }

  @Override
  public List<ShippingAddressResponseDto> getAllByCustomerId(Integer customerId) {
    log.debug("Getting all addresses for customer: {}", customerId);
    validateCustomerExists(customerId);

    List<ShippingAddress> addresses =
        shippingAddressRepository.findByCustomer_IdOrderByIsDefaultDescCreatedAtDesc(customerId);
    return shippingAddressMapper.toDtoList(addresses);
  }

  @Override
  public Page<ShippingAddressResponseDto> getAllByCustomerId(
      Integer customerId, Pageable pageable) {
    log.debug("Getting paged addresses for customer: {} with pageable: {}", customerId, pageable);
    validateCustomerExists(customerId);

    Page<ShippingAddress> addressPage =
        shippingAddressRepository.findByCustomer_Id(customerId, pageable);
    return addressPage.map(shippingAddressMapper::toDto);
  }

  @Override
  @Transactional
  public void delete(Integer id, Integer customerId) {
    log.info("Deleting shipping address with ID: {} for customer: {}", id, customerId);

    ShippingAddress address = findAddressById(id);
    validateAccessPermission(id, customerId);

    boolean wasDefault = address.getIsDefault();

    shippingAddressRepository.delete(address);

    if (wasDefault) {
      setNewDefaultAddress(customerId);
    }

    log.info("Successfully deleted shipping address with ID: {}", id);
  }

  @Override
  @Transactional
  public ShippingAddressResponseDto setDefaultAddress(Integer customerId, Integer addressId) {
    log.info("Setting default address: {} for customer: {}", addressId, customerId);

    validateCustomerExists(customerId);
    validateAccessPermission(addressId, customerId);

    clearDefaultAddressForCustomer(customerId);

    ShippingAddress address = findAddressById(addressId);
    address.setIsDefault(true);
    address.setUpdatedAt(LocalDateTime.now());

    ShippingAddress updatedAddress = shippingAddressRepository.save(address);
    log.info("Successfully set default address: {}", addressId);

    return shippingAddressMapper.toDto(updatedAddress);
  }

  @Override
  public ShippingAddressResponseDto getDefaultAddress(Integer customerId) {
    log.debug("Getting default address for customer: {}", customerId);
    validateCustomerExists(customerId);

    ShippingAddress defaultAddress =
        shippingAddressRepository
            .findDefaultByCustomerId(customerId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Khách hàng chưa có địa chỉ mặc định"));

    return shippingAddressMapper.toDto(defaultAddress);
  }

  @Override
  public boolean hasAccessToAddress(Integer addressId, Integer customerId) {
    return shippingAddressRepository.existsByIdAndCustomer_Id(addressId, customerId);
  }

  @Override
  public long countByCustomerId(Integer customerId) {
    validateCustomerExists(customerId);
    return shippingAddressRepository.countByCustomer_Id(customerId);
  }

  // Private helper methods

  private void validateCreateRequest(CreateShippingAddressDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (dto.getCustomerId() == null) {
      errors.put("customerId", "ID khách hàng không được để trống");
    } else if (!customerRepository.existsById(dto.getCustomerId())) {
      errors.put("customerId", "Khách hàng không tồn tại");
    }

    validateAddressFields(
        dto.getRecipientName(),
        dto.getRecipientPhoneNumber(),
        dto.getProvince(),
        dto.getDistrict(),
        dto.getWard(),
        dto.getDetailedAddress(),
        errors);

    if (!errors.isEmpty()) {
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }
  }

  private void validateUpdateRequest(UpdateShippingAddressDTO dto) {
    Map<String, String> errors = new HashMap<>();

    validateAddressFields(
        dto.getRecipientName(),
        dto.getRecipientPhoneNumber(),
        dto.getProvince(),
        dto.getDistrict(),
        dto.getWard(),
        dto.getDetailedAddress(),
        errors);

    if (!errors.isEmpty()) {
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }
  }

  private void validateAddressFields(
      String recipientName,
      String recipientPhoneNumber,
      String province,
      String district,
      String ward,
      String detailedAddress,
      Map<String, String> errors) {

    if (recipientName != null) {
      if (recipientName.trim().isEmpty()) {
        errors.put("recipientName", "Tên người nhận không được để trống");
      } else if (recipientName.length() < 2 || recipientName.length() > 100) {
        errors.put("recipientName", "Tên người nhận phải có từ 2-100 ký tự");
      }
    }

    if (recipientPhoneNumber != null) {
      if (recipientPhoneNumber.trim().isEmpty()) {
        errors.put("recipientPhoneNumber", "Số điện thoại không được để trống");
      } else if (!recipientPhoneNumber.matches("^0[0-9]{9}$")) {
        errors.put("recipientPhoneNumber", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
      }
    }

    if (province != null) {
      if (province.trim().isEmpty()) {
        errors.put("province", "Tỉnh/Thành phố không được để trống");
      } else if (province.length() < 2 || province.length() > 100) {
        errors.put("province", "Tên tỉnh/thành phố phải có từ 2-100 ký tự");
      }
    }

    if (district != null) {
      if (district.trim().isEmpty()) {
        errors.put("district", "Quận/Huyện không được để trống");
      } else if (district.length() < 2 || district.length() > 100) {
        errors.put("district", "Tên quận/huyện phải có từ 2-100 ký tự");
      }
    }

    if (ward != null) {
      if (ward.trim().isEmpty()) {
        errors.put("ward", "Phường/Xã không được để trống");
      } else if (ward.length() < 2 || ward.length() > 100) {
        errors.put("ward", "Tên phường/xã phải có từ 2-100 ký tự");
      }
    }

    if (detailedAddress != null) {
      if (detailedAddress.trim().isEmpty()) {
        errors.put("detailedAddress", "Địa chỉ chi tiết không được để trống");
      } else if (detailedAddress.length() < 5 || detailedAddress.length() > 255) {
        errors.put("detailedAddress", "Địa chỉ chi tiết phải có từ 5-255 ký tự");
      }
    }
  }

  private void validateCustomerExists(Integer customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + customerId);
    }
  }

  private void validateAccessPermission(Integer addressId, Integer customerId) {
    if (!hasAccessToAddress(addressId, customerId)) {
      throw new BusinessException("Bạn không có quyền truy cập địa chỉ này");
    }
  }

  private void validateAddressLimit(Integer customerId) {
    long currentCount = countByCustomerId(customerId);
    if (currentCount >= MAX_ADDRESSES_PER_CUSTOMER) {
      throw new BusinessException(
          "Bạn đã đạt giới hạn tối đa " + MAX_ADDRESSES_PER_CUSTOMER + " địa chỉ");
    }
  }

  private ShippingAddress findAddressById(Integer id) {
    return shippingAddressRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ với ID: " + id));
  }

  private Customer findCustomerById(Integer customerId) {
    return customerRepository
        .findById(customerId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));
  }

  private boolean isFirstAddress(Integer customerId) {
    return countByCustomerId(customerId) == 0;
  }

  private void clearDefaultAddressForCustomer(Integer customerId) {
    shippingAddressRepository.clearDefaultForCustomer(customerId);
  }

  private void setNewDefaultAddress(Integer customerId) {
    List<ShippingAddress> remainingAddresses =
        shippingAddressRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId);

    if (!remainingAddresses.isEmpty()) {
      ShippingAddress newDefault = remainingAddresses.get(0);
      newDefault.setIsDefault(true);
      newDefault.setUpdatedAt(LocalDateTime.now());
      shippingAddressRepository.save(newDefault);
      log.info("Set new default address: {} for customer: {}", newDefault.getId(), customerId);
    }
  }
}
