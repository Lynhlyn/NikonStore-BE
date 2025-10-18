package com.example.nikonbe.modules.staff.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.modules.staff.entity.Staff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsByUsernameAndIdNot(String username, Integer id);

  boolean existsByEmailAndIdNot(String email, Integer id);

  boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);

  Optional<Staff> findByUsername(String username);

  Optional<Staff> findByEmail(String email);

  Optional<Staff> findByPhoneNumber(String phoneNumber);

  List<Staff> findByStatus(Status status);

  Page<Staff> findByStatus(Status status, Pageable pageable);

  List<Staff> findByRole(UserRole role);

  @Query(
      "SELECT s FROM Staff s WHERE "
          + "(:fullName IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND "
          + "(:phoneNumber IS NULL OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) AND "
          + "(:role IS NULL OR s.role = :role) AND "
          + "(:status IS NULL OR s.status = :status)")
  List<Staff> findAllByFilters(
      @Param("fullName") String fullName,
      @Param("phoneNumber") String phoneNumber,
      @Param("role") UserRole role,
      @Param("status") Status status);

  @Query(
      "SELECT s FROM Staff s WHERE "
          + "(:fullName IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND "
          + "(:phoneNumber IS NULL OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) AND "
          + "(:role IS NULL OR s.role = :role) AND "
          + "(:status IS NULL OR s.status = :status)")
  Page<Staff> findAllByFiltersPaginated(
      @Param("fullName") String fullName,
      @Param("phoneNumber") String phoneNumber,
      @Param("role") UserRole role,
      @Param("status") Status status,
      Pageable pageable);
}
