package com.example.nikonbe.modules.review.service.interF;

import com.example.nikonbe.modules.review.dto.request.ReviewCreateDTO;
import com.example.nikonbe.modules.review.dto.request.ReviewUpdateDTO;
import com.example.nikonbe.modules.review.dto.response.ProductReviewSummaryDTO;
import com.example.nikonbe.modules.review.dto.response.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

  ReviewResponseDTO create(ReviewCreateDTO dto, Integer customerId);

  ReviewResponseDTO update(Integer id, ReviewUpdateDTO dto, Integer customerId);

  ReviewResponseDTO getById(Integer id);

  Page<ReviewResponseDTO> getByProductId(Integer productId, Integer status, Pageable pageable);

  Page<ReviewResponseDTO> getByCustomerId(Integer customerId, Pageable pageable);

  void delete(Integer id, Integer customerId);

  ProductReviewSummaryDTO getProductReviewSummary(Integer productId);

  ReviewResponseDTO updateStatus(Integer id, Integer status);

  java.util.List<ReviewResponseDTO> getByOrderId(Integer orderId);
}
