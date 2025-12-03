package com.example.nikonbe.modules.faq.repository;

import com.example.nikonbe.modules.faq.entity.FAQ;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Integer> {

  Page<FAQ> findByStatus(Boolean status, Pageable pageable);

  Page<FAQ> findByCategoryIdAndStatus(Integer categoryId, Boolean status, Pageable pageable);

  Page<FAQ> findByTagIdAndStatus(Integer tagId, Boolean status, Pageable pageable);

  @Query(
      "SELECT f FROM FAQ f "
          + "LEFT JOIN FETCH f.category "
          + "LEFT JOIN FETCH f.tag "
          + "WHERE f.status = :status "
          + "ORDER BY f.createdAt DESC")
  Page<FAQ> findByStatusWithRelations(@Param("status") Boolean status, Pageable pageable);

  @Query(
      "SELECT f FROM FAQ f "
          + "LEFT JOIN FETCH f.category "
          + "LEFT JOIN FETCH f.tag "
          + "WHERE f.id = :id")
  Optional<FAQ> findByIdWithRelations(@Param("id") Integer id);

  @Query(
      "SELECT f FROM FAQ f "
          + "LEFT JOIN FETCH f.category "
          + "LEFT JOIN FETCH f.tag "
          + "WHERE (:categoryId IS NULL OR f.category.id = :categoryId) AND "
          + "(:tagId IS NULL OR f.tag.id = :tagId) AND "
          + "(:status IS NULL OR f.status = :status) "
          + "ORDER BY f.createdAt DESC")
  Page<FAQ> findAllWithFilters(
      @Param("categoryId") Integer categoryId,
      @Param("tagId") Integer tagId,
      @Param("status") Boolean status,
      Pageable pageable);

  @Query(
      "SELECT f FROM FAQ f "
          + "LEFT JOIN FETCH f.category "
          + "LEFT JOIN FETCH f.tag "
          + "WHERE f.status = true "
          + "ORDER BY f.createdAt DESC")
  List<FAQ> findAllActiveWithRelations();
}


