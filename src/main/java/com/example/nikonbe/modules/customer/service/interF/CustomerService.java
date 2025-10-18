package com.example.nikonbe.modules.customer.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerFilterDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

  CustomerResponseDTO create(CustomerCreateDTO dto);

  CustomerResponseDTO update(Integer id, CustomerUpdateDTO dto);

  CustomerResponseDTO getById(Integer id);

  Page<CustomerResponseDTO> getAll(String keyword, Status status, Pageable pageable);

  Page<CustomerResponseDTO> getCustomersWithAdvancedFilters(
      CustomerFilterDTO filterDTO, Pageable pageable);

  void delete(Integer id);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  CustomerResponseDTO toggleStatus(Integer id, Status status);

  CustomerResponseDTO blockCustomer(Integer id, String reason);

  CustomerResponseDTO unblockCustomer(Integer id);
}
