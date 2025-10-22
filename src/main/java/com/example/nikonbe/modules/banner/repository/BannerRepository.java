package com.example.nikonbe.modules.banner.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.banner.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
  
  List<Banner> findByStatusAndIsActiveTrueOrderByDisplayOrderAsc(Status status);
  
  List<Banner> findByPositionAndStatusAndIsActiveTrueOrderByDisplayOrderAsc(
      String position, Status status);
  
  Page<Banner> findByStatusAndIsActiveTrueOrderByDisplayOrderAsc(
      Status status, Pageable pageable);
  
  @Query("SELECT b FROM Banner b WHERE " +
         "(:status IS NULL OR b.status = :status) AND " +
         "(:position IS NULL OR b.position = :position) AND " +
         "(:isActive IS NULL OR b.isActive = :isActive)")
  Page<Banner> findAllWithFilters(
      @Param("status") Status status,
      @Param("position") String position,
      @Param("isActive") Boolean isActive,
      Pageable pageable);
  
  @Query("SELECT b FROM Banner b WHERE " +
         "(:status IS NULL OR b.status = :status) AND " +
         "(:position IS NULL OR b.position = :position) AND " +
         "(:isActive IS NULL OR b.isActive = :isActive)")
  List<Banner> findAllWithFiltersList(
      @Param("status") Status status,
      @Param("position") String position,
      @Param("isActive") Boolean isActive);
  
  Optional<Banner> findByNameAndIdNot(String name, Long id);
  
  boolean existsByNameAndIdNot(String name, Long id);
}
