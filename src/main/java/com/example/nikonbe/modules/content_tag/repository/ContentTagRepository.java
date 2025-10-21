package com.example.nikonbe.modules.content_tag.repository;

import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentTagRepository extends JpaRepository<ContentTag, Integer> {
  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Integer id);

  List<ContentTag> findByNameContainingIgnoreCase(String name);

  Page<ContentTag> findByNameContainingIgnoreCase(String name, Pageable pageable);

  @Query(
      "SELECT c FROM ContentTag c WHERE "
          + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:slug IS NULL OR LOWER(c.slug) LIKE LOWER(CONCAT('%', :slug, '%')))AND "
          + "(:type IS NULL OR LOWER(c.type) LIKE LOWER(CONCAT('%', :type, '%')))")
  List<ContentTag> findAllByNameOrSlugOrType(
      @Param("name") String name, @Param("slug") String slug, @Param("type") String type);

  @Query(
      "SELECT c FROM ContentTag c WHERE "
          + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:slug IS NULL OR LOWER(c.slug) LIKE LOWER(CONCAT('%', :slug, '%')))AND "
          + "(:type IS NULL OR LOWER(c.type) LIKE LOWER(CONCAT('%', :type, '%')))")
  Page<ContentTag> findAllByNameOrSlugOrTypePage(
      @Param("name") String name,
      @Param("slug") String slug,
      @Param("type") String type,
      Pageable pageable);
}
