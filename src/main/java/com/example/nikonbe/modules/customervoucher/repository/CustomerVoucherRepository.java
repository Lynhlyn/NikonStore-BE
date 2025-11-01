package com.example.nikonbe.modules.customervoucher.repository;

import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customervoucher.entity.CustomerVoucher;
import com.example.nikonbe.modules.customervoucher.entity.CustomerVoucherId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerVoucherRepository
    extends JpaRepository<CustomerVoucher, CustomerVoucherId> {

  List<CustomerVoucher> findByCustomerId(Integer customerId);

  Page<CustomerVoucher> findByCustomerId(Integer customerId, Pageable pageable);

  @Query(
      "SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.usedAt IS NOT NULL")
  List<CustomerVoucher> findUsedVouchersByCustomerId(@Param("customerId") Integer customerId);

  @Query(
      "SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.usedAt IS NULL")
  List<CustomerVoucher> findUnusedVouchersByCustomerId(@Param("customerId") Integer customerId);

  boolean existsByIdCustomerIdAndIdVoucherId(Integer customerId, Long voucherId);

  Optional<CustomerVoucher> findByIdCustomerIdAndIdVoucherId(Integer customerId, Long voucherId);

  long countByCustomerId(Integer customerId);

  @Query(
      "SELECT COUNT(cv) FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.usedAt IS NOT NULL")
  long countUsedVouchersByCustomerId(@Param("customerId") Integer customerId);

  @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.voucher.id = :voucherId")
  Page<CustomerVoucher> findCustomersByVoucherId(
      @Param("voucherId") Long voucherId, Pageable pageable);

  @Query("SELECT cv.customer FROM CustomerVoucher cv WHERE cv.voucher.id = :voucherId")
  List<Customer> findCustomersByVoucherId(@Param("voucherId") Long voucherId);

  @Query("SELECT COUNT(cv) FROM CustomerVoucher cv WHERE cv.voucher.id = :voucherId")
  long countCustomersByVoucherId(@Param("voucherId") Long voucherId);

  @Modifying
  @Query("DELETE FROM CustomerVoucher cv WHERE cv.voucher.id = :voucherId")
  int deleteByVoucherId(@Param("voucherId") Long voucherId);
}
