package com.example.nikonbe.modules.attributes.strape_type.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.strape_type.entity.StrapType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrapTypeRepository extends JpaRepository<StrapType, Integer> {
  Optional<StrapType> findByName(String name);

  List<StrapType> findByStatus(Status status);

  Page<StrapType> findByStatus(Status status, Pageable pageable);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);

  // Search by name containing keyword (for search param)
  Page<StrapType> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

  List<StrapType> findByNameContainingIgnoreCase(String keyword);
}
