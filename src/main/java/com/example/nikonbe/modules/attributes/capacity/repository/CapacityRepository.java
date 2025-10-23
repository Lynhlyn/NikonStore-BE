package com.example.nikonbe.modules.attributes.capacity.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.capacity.entity.Capacity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacityRepository extends JpaRepository<Capacity, Integer> {
  Optional<Capacity> findByName(String name);

  List<Capacity> findByStatus(Status status);

  Page<Capacity> findByStatus(Status status, Pageable pageable);

  List<Capacity> findByNameContainingIgnoreCase(String keyword);

  Page<Capacity> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);
}
