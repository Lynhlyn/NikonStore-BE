package com.example.nikonbe.modules.banner.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.banner.entity.Banner;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

  List<Banner> findByStatusOrderByDisplayOrderAsc(Status status);

  List<Banner> findByPositionAndStatusOrderByDisplayOrderAsc(Integer position, Status status);

  Page<Banner> findByStatusOrderByDisplayOrderAsc(Status status, Pageable pageable);

  @Query(
      "SELECT b FROM Banner b WHERE "
          + "(:status IS NULL OR b.status = :status) AND "
          + "(:position IS NULL OR b.position = :position)")
  Page<Banner> findAllWithFilters(
      @Param("status") Status status, @Param("position") Integer position, Pageable pageable);

  @Query(
      "SELECT b FROM Banner b WHERE "
          + "(:status IS NULL OR b.status = :status) AND "
          + "(:position IS NULL OR b.position = :position)")
  List<Banner> findAllWithFiltersList(
      @Param("status") Status status, @Param("position") Integer position);

  Optional<Banner> findByNameAndIdNot(String name, Long id);

  boolean existsByNameAndIdNot(String name, Long id);
}
