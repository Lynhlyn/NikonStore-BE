package com.example.nikonbe.modules.staff.repository;

import com.example.nikonbe.modules.staff.entity.StaffToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffTokenRepository extends JpaRepository<StaffToken, Integer> {

  Optional<StaffToken> findByRefreshToken(String refreshToken);

  Optional<StaffToken> findByStaffId(Integer staffId);

  @Query(
      "SELECT st FROM StaffToken st WHERE st.tokenReset = :token AND st.expiresAt > :currentTime")
  Optional<StaffToken> findValidResetToken(
      @Param("token") String token, @Param("currentTime") LocalDateTime currentTime);

  @Modifying
  @Query("DELETE FROM StaffToken st WHERE st.staff.id = :staffId")
  void deleteByStaffId(@Param("staffId") Integer staffId);

  @Modifying
  @Query(
      "UPDATE StaffToken st SET st.tokenReset = :token, st.expiresAt = :expiresAt, st.updatedAt = :updatedAt WHERE st.staff.id = :staffId")
  void updateResetToken(
      @Param("staffId") Integer staffId,
      @Param("token") String token,
      @Param("expiresAt") LocalDateTime expiresAt,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("UPDATE StaffToken st SET st.tokenReset = NULL WHERE st.staff.id = :staffId")
  void clearResetToken(@Param("staffId") Integer staffId);

  @Modifying
  @Query(
      "UPDATE StaffToken st SET st.tokenReset = NULL WHERE st.tokenReset IS NOT NULL AND st.expiresAt < :currentTime")
  void clearExpiredResetTokens(@Param("currentTime") LocalDateTime currentTime);

  Optional<StaffToken> findByAccessToken(String accessToken);

  boolean existsByRefreshToken(String refreshToken);
}
