package com.example.nikonbe.modules.customer.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.customer.dto.request.ChangePasswordDTO;
import com.example.nikonbe.modules.customer.dto.request.CreateCustomerDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerClientUpdateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerFilterDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {

  CustomerResponseDTO create(CustomerCreateDTO dto);

  CustomerResponseDTO adminCreated(CreateCustomerDTO dto);

  CustomerResponseDTO update(Integer id, CustomerUpdateDTO dto);

  CustomerResponseDTO adminUpdate(Integer id, CustomerUpdateDTO dto);

  CustomerResponseDTO getById(Integer id);

  CustomerResponseDTO getCurrentUser(Authentication authentication);

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

  CustomerResponseDTO updateClientInfo(Integer id, CustomerClientUpdateDTO dto, MultipartFile image)
      throws IOException;

  void changePassword(Integer customerId, ChangePasswordDTO dto);

  void delete(Integer id, String reason);

  void verifyEmail(String token);

  void resendVerificationEmail(String email);
}
