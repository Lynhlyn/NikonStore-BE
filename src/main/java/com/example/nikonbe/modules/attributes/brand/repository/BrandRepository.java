package com.example.nikonbe.modules.attributes.brand.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.brand.entity.Brand;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
  Optional<Brand> findByName(String name);

  List<Brand> findByStatus(Status status);

  Page<Brand> findByStatus(Status status, Pageable pageable);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);
}
