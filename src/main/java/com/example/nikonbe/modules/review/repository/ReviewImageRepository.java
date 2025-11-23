package com.example.nikonbe.modules.review.repository;

import com.example.nikonbe.modules.review.entity.ReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {

  List<ReviewImage> findByReviewId(Integer reviewId);

  void deleteByReviewId(Integer reviewId);
}
