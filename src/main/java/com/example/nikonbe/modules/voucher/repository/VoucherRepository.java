package com.example.nikonbe.modules.voucher.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

  Optional<Voucher> findByCode(String code);

  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);

  @Query(
      "SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND v.isPublic = true AND v.startDate <= :now AND v.endDate >= :now")
  List<Voucher> findPublicActiveVouchers(@Param("now") LocalDateTime now);

  @Query(
      "SELECT v FROM Voucher v WHERE "
          + "(:code IS NULL OR LOWER(v.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND "
          + "(:status IS NULL OR v.status = :status) AND "
          + "(:discountType IS NULL OR v.discountType = :discountType) AND "
          + "(:isPublic IS NULL OR v.isPublic = :isPublic)")
  Page<Voucher> findVouchersWithFilters(
      @Param("code") String code,
      @Param("status") Status status,
      @Param("discountType") String discountType,
      @Param("isPublic") Boolean isPublic,
      Pageable pageable);

  @Query("SELECT v FROM Voucher v WHERE v.endDate < :now")
  List<Voucher> findExpiredVouchers(@Param("now") LocalDateTime now);

  List<Voucher> findByStatus(Status status);

  List<Voucher> findByDiscountType(String discountType);

  @Query(
      "SELECT COUNT(v) FROM Voucher v WHERE v.status = 'ACTIVE' AND v.startDate <= :now AND v.endDate >= :now")
  Long countActiveVouchers(@Param("now") LocalDateTime now);

  @Query(
      "SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND v.endDate BETWEEN :now AND :futureDate")
  List<Voucher> findVouchersExpiringWithin(
      @Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);
}
