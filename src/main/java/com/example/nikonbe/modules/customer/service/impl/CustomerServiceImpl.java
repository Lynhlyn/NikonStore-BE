package com.example.nikonbe.modules.customer.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.common.helper.cloudinary.service.ImageUploadService;
import com.example.nikonbe.modules.customer.dto.request.ChangePasswordDTO;
import com.example.nikonbe.modules.customer.dto.request.CreateCustomerDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerClientUpdateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerFilterDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.mapper.CustomerMapper;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.customer.service.interF.CustomerService;
import com.example.nikonbe.modules.shipping_address.mapper.ShippingAddressMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final PasswordEncoder passwordEncoder;
  private final ImageUploadService imageUploadService;
  private final ShippingAddressMapper shippingAddressMapper;

  @Override
  @Transactional
  public CustomerResponseDTO create(CustomerCreateDTO dto) {
    validateCreateRequest(dto);

    Customer customer = customerMapper.toEntity(dto);
    customer.setPassword(passwordEncoder.encode(dto.getPassword()));
    customer.setProvider("LOCAL");
    customer.setIsGuest(dto.getIsGuest() != null ? dto.getIsGuest() : false);

    Customer savedCustomer = customerRepository.save(customer);
    log.info("Created customer with ID: {}", savedCustomer.getId());

    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public CustomerResponseDTO adminCreated(CreateCustomerDTO dto) {
    validateAdminCreateRequest(dto);

    Customer customer = customerMapper.toEntity(dto);
    // Auto-generate password from phone number
    customer.setPassword(passwordEncoder.encode(dto.getPhoneNumber()));

    // Auto-generate username from phone number + timestamp if not provided
    if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
      String basePhone = dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : "user";
      String generated = basePhone + System.currentTimeMillis();
      if (customerRepository.existsByUsername(generated)) {
        generated = generated + (int) (Math.random() * 9000 + 1000);
      }
      customer.setUsername(generated);
      log.debug("Generated username for admin create: {}", generated);
    } else {
      customer.setUsername(dto.getUsername().trim());
    }

    // Auto-generate fullName from email if not provided
    if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
      if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
        String emailPrefix = dto.getEmail().split("@")[0];
        customer.setFullName(emailPrefix);
      }
    } else {
      customer.setFullName(dto.getFullName().trim());
    }

    customer.setStatus(dto.getStatus());
    customer.setProvider("LOCAL");
    customer.setIsGuest(dto.getIsGuest() != null ? dto.getIsGuest() : false);

    Customer savedCustomer = customerRepository.save(customer);
    log.info("Admin created customer with ID: {}", savedCustomer.getId());

    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public CustomerResponseDTO update(Integer id, CustomerUpdateDTO dto) {
    Customer customer = findCustomerById(id);
    validateUpdateRequest(dto, id);

    customerMapper.updateEntityFromDto(dto, customer);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Updated customer with ID: {}", id);
    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public CustomerResponseDTO adminUpdate(Integer id, CustomerUpdateDTO dto) {
    Customer customer = findCustomerById(id);
    validateUpdateRequest(dto, id);

    // Auto-generate username from phone number if not provided
    if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
      if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
        dto.setUsername(dto.getPhoneNumber().trim());
      }
    } else {
      dto.setUsername(dto.getUsername().trim());
    }

    // Auto-generate fullName from email if not provided
    if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
      if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
        String emailPrefix = dto.getEmail().split("@")[0];
        dto.setFullName(emailPrefix);
      }
    } else {
      dto.setFullName(dto.getFullName().trim());
    }

    customerMapper.updateEntityFromDto(dto, customer);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Admin updated customer with ID: {}", id);
    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional(readOnly = true)
  public CustomerResponseDTO getById(Integer id) {
    Customer customer = findCustomerById(id);
    // Fetch shipping addresses to avoid lazy loading issues
    if (customer.getShippingAddresses() != null) {
      customer.getShippingAddresses().size(); // Force fetch
    }
    CustomerResponseDTO dto = customerMapper.toDto(customer);
    // Map shipping addresses manually
    if (customer.getShippingAddresses() != null && !customer.getShippingAddresses().isEmpty()) {
      dto.setShippingAddresses(shippingAddressMapper.toDtoList(customer.getShippingAddresses()));
    }
    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public CustomerResponseDTO getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ValidationException("Người dùng chưa đăng nhập hoặc token không hợp lệ");
    }
    if (authentication.getName() == null) {
      throw new ValidationException("Thông tin xác thực không hợp lệ");
    }

    String username = authentication.getName();
    log.debug("Getting current customer info for username: {}", username);

    Customer customer =
        customerRepository
            .findByEmailOrUsername(username)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Không tìm thấy customer với thông tin: " + username));

    if (customer.getStatus() != Status.ACTIVE) {
      throw new ValidationException("Tài khoản không hoạt động");
    }

    log.info("Successfully retrieved current customer info for customer ID: {}", customer.getId());
    // Fetch shipping addresses to avoid lazy loading issues
    if (customer.getShippingAddresses() != null) {
      customer.getShippingAddresses().size(); // Force fetch
    }
    CustomerResponseDTO dto = customerMapper.toDto(customer);
    // Map shipping addresses manually
    if (customer.getShippingAddresses() != null && !customer.getShippingAddresses().isEmpty()) {
      dto.setShippingAddresses(shippingAddressMapper.toDtoList(customer.getShippingAddresses()));
    }
    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CustomerResponseDTO> getAll(String keyword, Status status, Pageable pageable) {
    Page<Customer> customerPage = customerRepository.findByFilters(keyword, status, pageable);
    return customerPage.map(customerMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CustomerResponseDTO> getCustomersWithAdvancedFilters(
      CustomerFilterDTO filterDTO, Pageable pageable) {
    Page<Customer> customerPage =
        customerRepository.findByAdvancedFilters(
            filterDTO.getKeyword(),
            filterDTO.getStatus(),
            filterDTO.getEmail(),
            filterDTO.getPhoneNumber(),
            filterDTO.getFullName(),
            filterDTO.getGender(),
            filterDTO.getProvider(),
            filterDTO.getIsGuest(),
            filterDTO.getCreatedFromDate(),
            filterDTO.getCreatedToDate(),
            pageable);
    return customerPage.map(customerMapper::toDto);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    delete(id, "Customer requested deletion");
  }

  @Override
  @Transactional
  public void delete(Integer id, String reason) {
    Customer customer = findCustomerById(id);
    customer.setStatus(Status.DELETED);
    customerRepository.save(customer);
    log.info("Soft deleted customer with ID: {}, Reason: {}", id, reason);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    return customerRepository.existsByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return customerRepository.existsByEmail(email);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByPhoneNumber(String phoneNumber) {
    return customerRepository.existsByPhoneNumber(phoneNumber);
  }

  @Override
  @Transactional
  public CustomerResponseDTO toggleStatus(Integer id, Status status) {
    Customer customer = findCustomerById(id);
    customer.setStatus(status);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Changed customer status - ID: {}, Status: {}", id, status);
    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public CustomerResponseDTO blockCustomer(Integer id, String reason) {
    Customer customer = findCustomerById(id);

    // Validate reason
    if (reason == null || reason.trim().isEmpty()) {
      Map<String, String> errors = new HashMap<>();
      errors.put("reason", "Lý do khoá tài khoản không được để trống.");
      throw new ValidationException("Dữ liệu không hợp lệ.", errors);
    }

    // Check if already blocked
    if (customer.getStatus() == Status.BLOCKED) {
      Map<String, String> errors = new HashMap<>();
      errors.put("status", "Tài khoản đã bị khoá trước đó.");
      throw new ValidationException("Dữ liệu không hợp lệ.", errors);
    }

    customer.setStatus(Status.BLOCKED);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Blocked customer - ID: {}, Reason: {}", id, reason);
    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public CustomerResponseDTO unblockCustomer(Integer id) {
    Customer customer = findCustomerById(id);

    // Check if not blocked
    if (customer.getStatus() != Status.BLOCKED) {
      Map<String, String> errors = new HashMap<>();
      errors.put("status", "Tài khoản hiện không ở trạng thái bị khoá.");
      throw new ValidationException("Dữ liệu không hợp lệ.", errors);
    }

    customer.setStatus(Status.ACTIVE);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Unblocked customer - ID: {}", id);
    return customerMapper.toDto(savedCustomer);
  }

  private Customer findCustomerById(Integer id) {
    return customerRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
  }

  private void validateCreateRequest(CustomerCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (customerRepository.existsByUsername(dto.getUsername())) {
      errors.put("username", "Username already exists");
    }

    if (customerRepository.existsByEmail(dto.getEmail())) {
      errors.put("email", "Email already exists");
    }

    if (customerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
      errors.put("phoneNumber", "Phone number already exists");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
  }

  private void validateUpdateRequest(CustomerUpdateDTO dto, Integer customerId) {
    Map<String, String> errors = new HashMap<>();

    if (dto.getUsername() != null
        && customerRepository.existsByUsernameAndIdNot(dto.getUsername(), customerId)) {
      errors.put("username", "Username already exists");
    }

    if (dto.getEmail() != null
        && customerRepository.existsByEmailAndIdNot(dto.getEmail(), customerId)) {
      errors.put("email", "Email already exists");
    }

    if (dto.getPhoneNumber() != null
        && customerRepository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), customerId)) {
      errors.put("phoneNumber", "Phone number already exists");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
  }

  private void validateAdminCreateRequest(CreateCustomerDTO dto) {
    Map<String, String> errors = new HashMap<>();

    // Validate username only if provided
    if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
      if (customerRepository.existsByUsername(dto.getUsername().trim())) {
        errors.put("username", "Username already exists");
      }
    }

    // Validate email (required)
    if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
      errors.put("email", "Email không được để trống.");
    } else if (customerRepository.existsByEmail(dto.getEmail().trim())) {
      errors.put("email", "Email already exists");
    }

    // Validate phone number (required)
    if (dto.getPhoneNumber() == null || dto.getPhoneNumber().trim().isEmpty()) {
      errors.put("phoneNumber", "Số điện thoại không được để trống.");
    } else if (customerRepository.existsByPhoneNumber(dto.getPhoneNumber().trim())) {
      errors.put("phoneNumber", "Phone number already exists");
    }

    // Validate status
    if (dto.getStatus() == null) {
      errors.put("status", "Trạng thái không được để trống.");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
  }

  @Override
  @Transactional
  public CustomerResponseDTO updateClientInfo(
      Integer id, CustomerClientUpdateDTO dto, MultipartFile image) throws IOException {
    log.info("Updating client info for customer ID: {}", id);

    Customer customer = findCustomerById(id);
    validateClientUpdateRequest(dto, id);

    updateClientFields(customer, dto, image != null && !image.isEmpty());

    if (image != null && !image.isEmpty()) {
      log.debug("Uploading new image");
      String imageUrl = imageUploadService.uploadImage(image, "customer");
      customer.setUrlImage(imageUrl);
      log.info("Updated image URL from Cloudinary: {}", imageUrl);
    }

    Customer savedCustomer = customerRepository.save(customer);
    log.info(
        "Customer info updated - ID: {}, urlImage: {}",
        savedCustomer.getId(),
        savedCustomer.getUrlImage());

    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public void changePassword(Integer customerId, ChangePasswordDTO dto) {
    Customer customer = findCustomerById(customerId);
    validatePasswordChange(dto, customer);

    customer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    customerRepository.save(customer);

    log.info("Password changed for customer ID: {}", customerId);
  }

  private void updateClientFields(
      Customer customer, CustomerClientUpdateDTO dto, boolean hasUploadedImage) {
    if (dto.getUsername() != null) {
      customer.setUsername(dto.getUsername().trim());
    }
    if (dto.getEmail() != null) {
      customer.setEmail(dto.getEmail().trim());
    }
    if (dto.getFullName() != null) {
      customer.setFullName(dto.getFullName().trim());
    }
    if (dto.getPhoneNumber() != null) {
      customer.setPhoneNumber(dto.getPhoneNumber().trim());
    }
    if (dto.getDateOfBirth() != null) {
      customer.setDateOfBirth(dto.getDateOfBirth());
    }
    if (dto.getGender() != null) {
      customer.setGender(dto.getGender().trim());
    }
    if (!hasUploadedImage && dto.getUrlImage() != null) {
      customer.setUrlImage(dto.getUrlImage());
    }
  }

  private void validateClientUpdateRequest(CustomerClientUpdateDTO dto, Integer customerId) {
    Map<String, String> errors = new HashMap<>();

    if (dto.getUsername() != null) {
      if (customerRepository.existsByUsernameAndIdNot(dto.getUsername(), customerId)) {
        errors.put("username", "Username already exists");
      }
    }

    if (dto.getEmail() != null) {
      if (customerRepository.existsByEmailAndIdNot(dto.getEmail(), customerId)) {
        errors.put("email", "Email already exists");
      }
    }

    if (dto.getPhoneNumber() != null) {
      if (customerRepository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), customerId)) {
        errors.put("phoneNumber", "Phone number already exists");
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
  }

  private void validatePasswordChange(ChangePasswordDTO dto, Customer customer) {
    Map<String, String> errors = new HashMap<>();

    if (!passwordEncoder.matches(dto.getCurrentPassword(), customer.getPassword())) {
      errors.put("currentPassword", "Current password is incorrect");
    }

    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
      errors.put("confirmPassword", "Confirm password does not match");
    }

    if (passwordEncoder.matches(dto.getNewPassword(), customer.getPassword())) {
      errors.put("newPassword", "New password must be different from current password");
    }

    if (dto.getNewPassword().length() < 8 || dto.getNewPassword().length() > 32) {
      errors.put("newPassword", "Password must be between 8-32 characters");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Password change validation failed", errors);
    }
  }
}
