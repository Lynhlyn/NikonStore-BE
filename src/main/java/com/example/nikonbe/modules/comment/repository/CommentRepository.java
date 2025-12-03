package com.example.nikonbe.modules.comment.repository;

import com.example.nikonbe.modules.comment.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

  Page<Comment> findByBlogId(Integer blogId, Pageable pageable);

  Page<Comment> findByBlogIdAndStatus(Integer blogId, Boolean status, Pageable pageable);

  List<Comment> findByBlogIdAndParentIsNullAndStatus(Integer blogId, Boolean status);

  List<Comment> findByParentId(Integer parentId);

  Page<Comment> findByCustomerId(Integer customerId, Pageable pageable);

  @Query(
      "SELECT c FROM Comment c "
          + "LEFT JOIN FETCH c.customer "
          + "LEFT JOIN FETCH c.staff "
          + "LEFT JOIN FETCH c.blog "
          + "LEFT JOIN FETCH c.parent "
          + "WHERE c.id = :id")
  Optional<Comment> findByIdWithRelations(@Param("id") Integer id);

  @Query(
      "SELECT c FROM Comment c "
          + "LEFT JOIN FETCH c.customer "
          + "LEFT JOIN FETCH c.staff "
          + "LEFT JOIN FETCH c.blog "
          + "LEFT JOIN FETCH c.parent "
          + "WHERE c.blog.id = :blogId AND c.parent IS NULL "
          + "ORDER BY c.createdAt DESC")
  List<Comment> findTopLevelCommentsByBlogId(@Param("blogId") Integer blogId);

  @Query(
      "SELECT c FROM Comment c "
          + "LEFT JOIN FETCH c.customer "
          + "LEFT JOIN FETCH c.staff "
          + "LEFT JOIN FETCH c.blog "
          + "LEFT JOIN FETCH c.parent "
          + "WHERE c.blog.id = :blogId AND c.parent IS NULL AND c.status = :status "
          + "ORDER BY c.createdAt DESC")
  List<Comment> findTopLevelCommentsByBlogIdAndStatus(
      @Param("blogId") Integer blogId, @Param("status") Boolean status);

  @Query(
      "SELECT c FROM Comment c "
          + "LEFT JOIN FETCH c.customer "
          + "LEFT JOIN FETCH c.staff "
          + "LEFT JOIN FETCH c.blog "
          + "LEFT JOIN FETCH c.parent "
          + "WHERE c.parent.id = :parentId "
          + "ORDER BY c.createdAt ASC")
  List<Comment> findRepliesByParentId(@Param("parentId") Integer parentId);

  @Query(
      "SELECT c FROM Comment c "
          + "LEFT JOIN FETCH c.customer "
          + "LEFT JOIN FETCH c.staff "
          + "LEFT JOIN FETCH c.blog "
          + "LEFT JOIN FETCH c.parent "
          + "WHERE (:blogId IS NULL OR c.blog.id = :blogId) AND "
          + "(:status IS NULL OR c.status = :status) "
          + "ORDER BY c.createdAt DESC")
  Page<Comment> findAllWithFilters(
      @Param("blogId") Integer blogId, @Param("status") Boolean status, Pageable pageable);
}


