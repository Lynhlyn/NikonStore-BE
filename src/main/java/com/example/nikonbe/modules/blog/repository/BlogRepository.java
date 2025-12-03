package com.example.nikonbe.modules.blog.repository;

import com.example.nikonbe.modules.blog.entity.Blog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

  Optional<Blog> findBySlug(String slug);

  Page<Blog> findByIsPublished(Boolean isPublished, Pageable pageable);

  Page<Blog> findByCategoryIdAndIsPublished(Integer categoryId, Boolean isPublished, Pageable pageable);

  Page<Blog> findByTagIdAndIsPublished(Integer tagId, Boolean isPublished, Pageable pageable);

  Page<Blog> findByStaffId(Integer staffId, Pageable pageable);

  @Query(
      "SELECT b FROM Blog b "
          + "LEFT JOIN FETCH b.staff "
          + "LEFT JOIN FETCH b.category "
          + "LEFT JOIN FETCH b.tag "
          + "WHERE b.isPublished = :isPublished "
          + "ORDER BY b.createdAt DESC")
  Page<Blog> findByIsPublishedWithRelations(
      @Param("isPublished") Boolean isPublished, Pageable pageable);

  @Query(
      "SELECT b FROM Blog b "
          + "LEFT JOIN FETCH b.staff "
          + "LEFT JOIN FETCH b.category "
          + "LEFT JOIN FETCH b.tag "
          + "WHERE b.id = :id")
  Optional<Blog> findByIdWithRelations(@Param("id") Integer id);

  @Query(
      "SELECT b FROM Blog b "
          + "LEFT JOIN FETCH b.staff "
          + "LEFT JOIN FETCH b.category "
          + "LEFT JOIN FETCH b.tag "
          + "WHERE b.slug = :slug")
  Optional<Blog> findBySlugWithRelations(@Param("slug") String slug);

  @Query(
      "SELECT b FROM Blog b "
          + "LEFT JOIN FETCH b.staff "
          + "LEFT JOIN FETCH b.category "
          + "LEFT JOIN FETCH b.tag "
          + "WHERE (:categoryId IS NULL OR b.category.id = :categoryId) AND "
          + "(:tagId IS NULL OR b.tag.id = :tagId) AND "
          + "(:staffId IS NULL OR b.staff.id = :staffId) AND "
          + "(:isPublished IS NULL OR b.isPublished = :isPublished) AND "
          + "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "ORDER BY b.createdAt DESC")
  Page<Blog> findAllWithFilters(
      @Param("categoryId") Integer categoryId,
      @Param("tagId") Integer tagId,
      @Param("staffId") Integer staffId,
      @Param("isPublished") Boolean isPublished,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Modifying
  @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
  void incrementViewCount(@Param("id") Integer id);
}


