package com.example.nikonbe.modules.attributes.category.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.category.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
  Optional<Category> findByName(String name);

  List<Category> findByStatus(Status status);

  Page<Category> findByStatus(Status status, Pageable pageable);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);

  Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Category> findByNameContainingIgnoreCaseAndStatus(
      String name, Status status, Pageable pageable);
}
