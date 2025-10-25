package com.example.nikonbe.modules.attributes.material.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.material.entity.Material;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
  Optional<Material> findByName(String name);

  List<Material> findByStatus(Status status);

  Page<Material> findByStatus(Status status, Pageable pageable);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);

  List<Material> findByNameContainingIgnoreCase(String keyword);

  Page<Material> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
