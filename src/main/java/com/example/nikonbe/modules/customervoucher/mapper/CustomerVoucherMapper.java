package com.example.nikonbe.modules.customervoucher.mapper;

import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customervoucher.dto.response.CustomerVoucherResponseDTO;
import com.example.nikonbe.modules.customervoucher.entity.CustomerVoucher;
import com.example.nikonbe.modules.customervoucher.entity.CustomerVoucherId;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import com.example.nikonbe.modules.voucher.mapper.VoucherMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {VoucherMapper.class})
public abstract class CustomerVoucherMapper {

  @Autowired protected VoucherMapper voucherMapper;

  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(
      target = "customerName",
      expression = "java(getCustomerName(customerVoucher.getCustomer()))")
  @Mapping(
      target = "voucher",
      expression = "java(voucherMapper.toDto(customerVoucher.getVoucher()))")
  @Mapping(target = "used", expression = "java(customerVoucher.getUsedAt() != null)")
  public abstract CustomerVoucherResponseDTO toDto(CustomerVoucher customerVoucher);

  public abstract List<CustomerVoucherResponseDTO> toDtoList(
      List<CustomerVoucher> customerVouchers);

  public CustomerVoucher createEntity(Customer customer, Voucher voucher) {
    if (customer == null || voucher == null) {
      return null;
    }

    CustomerVoucherId id = new CustomerVoucherId(customer.getId(), voucher.getId());

    return CustomerVoucher.builder().id(id).customer(customer).voucher(voucher).build();
  }

  protected String getCustomerName(Customer customer) {
    if (customer == null) {
      return null;
    }

    return customer.getFullName() != null ? customer.getFullName() : customer.getUsername();
  }
}
