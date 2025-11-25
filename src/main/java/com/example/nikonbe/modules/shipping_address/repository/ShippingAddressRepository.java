package com.example.nikonbe.modules.shipping_address.repository;

import com.example.nikonbe.modules.shipping_address.entity.ShippingAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link com.example.nikonbe.modules.shipping_address.entity.ShippingAddress}
 *
 * <p>Repository xử lý truy vấn liên quan đến bảng ShippingAddress.
 */
@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Integer> {

  List<ShippingAddress> findByCustomer_Id(Integer customerId);

  List<ShippingAddress> findByCustomer_IdOrderByIsDefaultDescCreatedAtDesc(Integer customerId);

  List<ShippingAddress> findByCustomer_IdOrderByCreatedAtDesc(Integer customerId);

  Page<ShippingAddress> findByCustomer_Id(Integer customerId, Pageable pageable);

  @Query(
      "SELECT sa FROM ShippingAddress sa WHERE sa.customer.id = :customerId AND sa.isDefault = true")
  Optional<ShippingAddress> findDefaultByCustomerId(@Param("customerId") Integer customerId);

  @Modifying
  @Query("UPDATE ShippingAddress sa SET sa.isDefault = false WHERE sa.customer.id = :customerId")
  void clearDefaultForCustomer(@Param("customerId") Integer customerId);

  @Query("SELECT sa FROM ShippingAddress sa WHERE sa.id = :id AND sa.customer.id = :customerId")
  Optional<ShippingAddress> findByIdAndCustomerId(
      @Param("id") Integer id, @Param("customerId") Integer customerId);

  boolean existsByIdAndCustomer_Id(Integer id, Integer customerId);

  long countByCustomer_Id(Integer customerId);
}
