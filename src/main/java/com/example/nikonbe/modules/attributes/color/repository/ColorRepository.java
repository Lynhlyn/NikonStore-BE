package com.example.nikonbe.modules.attributes.color.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.color.entity.Color;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
  Optional<Color> findByName(String name);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);

  List<Color> findByNameContainingIgnoreCase(String name);

  Page<Color> findByNameContainingIgnoreCase(String name, Pageable pageable);

  List<Color> findByStatus(Status status);

  Page<Color> findByStatus(Status status, Pageable pageable);

  Page<Color> findByNameContainingIgnoreCaseAndStatus(
      String name, Status status, Pageable pageable);

  List<Color> findByNameContainingIgnoreCaseAndStatus(String name, Status status);
}
