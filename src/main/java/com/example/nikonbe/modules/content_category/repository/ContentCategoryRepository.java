package com.example.nikonbe.modules.content_category.repository;

import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Integer> {
  boolean existsByName(String name);

  boolean existsBySlug(String slug);

  boolean existsByNameAndIdNot(String name, Integer id);

  boolean existsBySlugAndIdNot(String slug, Integer id);

  List<ContentCategory> findByNameContainingIgnoreCase(String name);

  List<ContentCategory> findBySlugContainingIgnoreCase(String slug);

  Page<ContentCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

  @Query(
      "SELECT c FROM ContentCategory c WHERE "
          + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:slug IS NULL OR LOWER(c.slug) LIKE LOWER(CONCAT('%', :slug, '%')))AND "
          + "(:type IS NULL OR LOWER(c.type) LIKE LOWER(CONCAT('%', :type, '%')))")
  List<ContentCategory> findAllByNameOrSlugOrType(
      @Param("name") String name, @Param("slug") String slug, @Param("type") String type);

  @Query(
      "SELECT c FROM ContentCategory c WHERE "
          + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:slug IS NULL OR LOWER(c.slug) LIKE LOWER(CONCAT('%', :slug, '%')))AND "
          + "(:type IS NULL OR LOWER(c.type) LIKE LOWER(CONCAT('%', :type, '%')))")
  Page<ContentCategory> findAllByNameOrSlugOrTypePage(
      @Param("name") String name,
      @Param("slug") String slug,
      @Param("type") String type,
      Pageable pageable);
}
