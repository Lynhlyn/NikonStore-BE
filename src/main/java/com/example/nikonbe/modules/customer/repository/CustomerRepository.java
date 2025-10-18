package com.example.nikonbe.modules.customer.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.customer.entity.Customer;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

  boolean existsByUsername(String username);

  boolean existsByUsernameAndIdNot(String username, Integer id);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Integer id);

  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);

  Optional<Customer> findByEmail(String email);

  Optional<Customer> findByPhoneNumber(String phoneNumber);

  Optional<Customer> findByUsername(String username);

  Optional<Customer> findByEmailOrUsername(@Param("keyword") String keyword);

  Optional<Customer> findByProviderAndProviderId(String provider, String providerId);

  Optional<Customer> findByEmailAndProvider(String email, String provider);

  @Query(
      "SELECT c FROM Customer c WHERE "
          + "(:keyword IS NULL OR "
          + "LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:status IS NULL OR c.status = :status) "
          + "AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) "
          + "AND (:phoneNumber IS NULL OR c.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) "
          + "AND (:fullName IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) "
          + "AND (:gender IS NULL OR LOWER(c.gender) LIKE LOWER(CONCAT('%', :gender, '%'))) "
          + "AND (:provider IS NULL OR LOWER(c.provider) = LOWER(:provider)) "
          + "AND (:isGuest IS NULL OR c.isGuest = :isGuest) "
          + "AND (:createdFromDate IS NULL OR DATE(c.createdAt) >= :createdFromDate) "
          + "AND (:createdToDate IS NULL OR DATE(c.createdAt) <= :createdToDate)")
  Page<Customer> findByAdvancedFilters(
      @Param("keyword") String keyword,
      @Param("status") Status status,
      @Param("email") String email,
      @Param("phoneNumber") String phoneNumber,
      @Param("fullName") String fullName,
      @Param("gender") String gender,
      @Param("provider") String provider,
      @Param("isGuest") Boolean isGuest,
      @Param("createdFromDate") LocalDate createdFromDate,
      @Param("createdToDate") LocalDate createdToDate,
      Pageable pageable);

  @Query(
      "SELECT c FROM Customer c WHERE "
          + "(:keyword IS NULL OR "
          + "LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:status IS NULL OR c.status = :status)")
  Page<Customer> findByFilters(
      @Param("keyword") String keyword, @Param("status") Status status, Pageable pageable);
}
