package com.example.nikonbe.modules.customer.repository;

import com.example.nikonbe.modules.customer.entity.CustomerToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerTokenRepository extends JpaRepository<CustomerToken, Integer> {

  Optional<CustomerToken> findByRefreshToken(String refreshToken);

  Optional<CustomerToken> findByCustomerId(Integer customerId);

  Optional<CustomerToken> findByTokenReset(String token);

  @Transactional
  @Modifying
  @Query("delete from CustomerToken c where c.customer.id = ?1")
  void deleteByCustomerId(Integer customerId);

  @Query(
      "SELECT ct FROM CustomerToken ct WHERE "
          + "ct.tokenReset = :tokenReset AND "
          + "ct.expiresAt > :currentTime")
  Optional<CustomerToken> findValidResetToken(
      @Param("tokenReset") String tokenReset, @Param("currentTime") LocalDateTime currentTime);

  @Modifying
  @Query(
      "UPDATE CustomerToken ct SET "
          + "ct.tokenReset = :tokenReset,"
          + "ct.expiresAt = :expiresAt,"
          + "ct.updatedAt = :updatedAt "
          + "WHERE ct.customer.id = :customerId")
  void updateResetToken(
      @Param("customerId") Integer customerId,
      @Param("tokenReset") String tokenReset,
      @Param("expiresAt") LocalDateTime expiresAt,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("UPDATE CustomerToken ct SET ct.tokenReset = NULL WHERE ct.customer.id = :customerId")
  void clearResetToken(@Param("customerId") Integer customerId);

  @Modifying
  @Query("UPDATE CustomerToken ct SET ct.tokenReset = NULL WHERE ct.expiresAt < :currentTime")
  void clearExpiredResetTokens(@Param("currentTime") LocalDateTime currentTime);
}
