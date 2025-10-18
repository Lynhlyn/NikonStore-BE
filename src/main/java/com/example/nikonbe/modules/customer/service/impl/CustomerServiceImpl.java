package com.example.nikonbe.modules.customer.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerFilterDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.mapper.CustomerMapper;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.customer.service.interF.CustomerService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final PasswordEncoder passwordEncoder;

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
  public CustomerResponseDTO update(Integer id, CustomerUpdateDTO dto) {
    Customer customer = findCustomerById(id);
    validateUpdateRequest(dto, id);

    customerMapper.updateEntityFromDto(dto, customer);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Updated customer with ID: {}", id);
    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional(readOnly = true)
  public CustomerResponseDTO getById(Integer id) {
    Customer customer = findCustomerById(id);
    return customerMapper.toDto(customer);
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
    Customer customer = findCustomerById(id);
    customer.setStatus(Status.DELETED);
    customerRepository.save(customer);

    log.info("Soft deleted customer with ID: {}", id);
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

    if (customer.getStatus() == Status.DELETED) {
      Map<String, String> errors = new HashMap<>();
      errors.put("status", "Cannot block a deleted customer");
      throw new ValidationException("Invalid operation", errors);
    }

    customer.setStatus(Status.INACTIVE);
    Customer savedCustomer = customerRepository.save(customer);

    log.info("Blocked customer - ID: {}, Reason: {}", id, reason);
    return customerMapper.toDto(savedCustomer);
  }

  @Override
  @Transactional
  public CustomerResponseDTO unblockCustomer(Integer id) {
    Customer customer = findCustomerById(id);
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

    if (customerRepository.existsByUsernameAndIdNot(dto.getUsername(), customerId)) {
      errors.put("username", "Username already exists");
    }

    if (customerRepository.existsByEmailAndIdNot(dto.getEmail(), customerId)) {
      errors.put("email", "Email already exists");
    }

    if (customerRepository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), customerId)) {
      errors.put("phoneNumber", "Phone number already exists");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }
  }
}
