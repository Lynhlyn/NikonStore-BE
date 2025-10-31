package com.example.nikonbe.modules.customervoucher.service.interF;

import com.example.nikonbe.modules.customervoucher.dto.request.CustomerVoucherAssignDTO;
import com.example.nikonbe.modules.customervoucher.dto.response.CustomerVoucherResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerVoucherService {

  List<CustomerVoucherResponseDTO> assignVouchersToCustomer(CustomerVoucherAssignDTO dto);

  void assignVoucherToCustomers(Long voucherId, List<Integer> customerIds);

  List<CustomerVoucherResponseDTO> getVouchersByCustomerId(Integer customerId);

  Page<CustomerVoucherResponseDTO> getVouchersByCustomerIdPaginated(
      Integer customerId, Pageable pageable);

  List<CustomerVoucherResponseDTO> getUsedVouchersByCustomerId(Integer customerId);

  List<CustomerVoucherResponseDTO> getUnusedVouchersByCustomerId(Integer customerId);

  boolean customerHasVoucher(Integer customerId, Long voucherId);

  CustomerVoucherResponseDTO useVoucher(Integer customerId, Long voucherId);

  void removeVoucherFromCustomer(Integer customerId, Long voucherId);

  long countVouchersByCustomerId(Integer customerId);

  long countUsedVouchersByCustomerId(Integer customerId);

  Page<CustomerVoucherResponseDTO> getCustomersByVoucherIdPaginated(
      Long voucherId, Pageable pageable);
}
