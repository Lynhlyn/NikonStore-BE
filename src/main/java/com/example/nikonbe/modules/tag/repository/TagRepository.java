package com.example.nikonbe.modules.tag.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.tag.entity.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

  boolean existsByName(String name);

  boolean existsBySlug(String slug);

  @Query(
      "SELECT t FROM Tag t WHERE "
          + "(?1 IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', ?1, '%'))) AND "
          + "(?2 IS NULL OR LOWER(t.slug) LIKE LOWER(CONCAT('%', ?2, '%'))) AND "
          + "(?3 IS NULL OR t.status = ?3)")
  List<Tag> findAllByFilters(String name, String slug, Status status);

  @Query(
      "SELECT t FROM Tag t WHERE "
          + "(?1 IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', ?1, '%'))) AND "
          + "(?2 IS NULL OR LOWER(t.slug) LIKE LOWER(CONCAT('%', ?2, '%'))) AND "
          + "(?3 IS NULL OR t.status = ?3)")
  Page<Tag> findAllPaginated(String name, String slug, Status status, Pageable pageable);

  List<Tag> findByStatus(Status status);
}
