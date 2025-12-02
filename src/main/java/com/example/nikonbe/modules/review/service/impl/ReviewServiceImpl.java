package com.example.nikonbe.modules.review.service.impl;

import com.example.nikonbe.common.exceptions.BadRequestException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.UnauthorizedException;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.order_detail.repository.OrderDetailRepository;
import com.example.nikonbe.modules.product.repository.ProductRepository;
import com.example.nikonbe.modules.review.dto.request.ReviewCreateDTO;
import com.example.nikonbe.modules.review.dto.request.ReviewUpdateDTO;
import com.example.nikonbe.modules.review.dto.response.ProductReviewSummaryDTO;
import com.example.nikonbe.modules.review.dto.response.ReviewResponseDTO;
import com.example.nikonbe.modules.review.entity.Review;
import com.example.nikonbe.modules.review.entity.ReviewImage;
import com.example.nikonbe.modules.review.mapper.ReviewMapper;
import com.example.nikonbe.modules.review.repository.ReviewImageRepository;
import com.example.nikonbe.modules.review.repository.ReviewRepository;
import com.example.nikonbe.modules.review.service.interF.ReviewService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewImageRepository reviewImageRepository;
  private final ReviewMapper reviewMapper;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final OrderDetailRepository orderDetailRepository;

  @Override
  public ReviewResponseDTO create(ReviewCreateDTO dto, Integer customerId) {
    productRepository
        .findById(dto.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));

    OrderDetail orderDetail = null;
    if (dto.getOrderDetailId() != null) {
      orderDetail =
          orderDetailRepository
              .findById(dto.getOrderDetailId())
              .orElseThrow(() -> new ResourceNotFoundException("Chi tiết đơn hàng không tồn tại"));
      
      boolean exists =
          reviewRepository.existsByProductIdAndCustomerIdAndOrderDetailId(
              dto.getProductId(), customerId, dto.getOrderDetailId());
      if (exists) {
        throw new BadRequestException("Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi");
      }
    }

    Review review = reviewMapper.toEntity(dto);
    review.setCustomer(customer);
    
    if (orderDetail != null && orderDetail.getProductDetail() != null) {
      review.setProductDetail(orderDetail.getProductDetail());
    } else {
      throw new BadRequestException("Không thể xác định chi tiết sản phẩm từ đơn hàng");
    }

    Review savedReview = reviewRepository.save(review);

    if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
      List<ReviewImage> reviewImages =
          dto.getImageUrls().stream()
              .map(
                  url -> {
                    ReviewImage image = new ReviewImage();
                    image.setReview(savedReview);
                    image.setImageUrl(url);
                    return image;
                  })
              .collect(Collectors.toList());
      reviewImageRepository.saveAll(reviewImages);
    }

    Review reviewWithRelations =
        reviewRepository
            .findByIdWithRelations(savedReview.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
    
    reviewWithRelations.setReviewImages(
        reviewImageRepository.findByReviewId(reviewWithRelations.getId()));
    
    return reviewMapper.toDto(reviewWithRelations);
  }

  @Override
  public ReviewResponseDTO update(Integer id, ReviewUpdateDTO dto, Integer customerId) {
    Review review =
        reviewRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại"));

    if (!review.getCustomer().getId().equals(customerId)) {
      throw new UnauthorizedException("Bạn không có quyền chỉnh sửa đánh giá này");
    }

    reviewMapper.updateEntityFromDto(dto, review);

    if (dto.getStatus() != null) {
      review.setStatus(dto.getStatus());
    }

    if (dto.getImageUrls() != null) {
      reviewImageRepository.deleteByReviewId(id);
      if (!dto.getImageUrls().isEmpty()) {
        List<ReviewImage> reviewImages =
            dto.getImageUrls().stream()
                .map(
                    url -> {
                      ReviewImage image = new ReviewImage();
                      image.setReview(review);
                      image.setImageUrl(url);
                      return image;
                    })
                .collect(Collectors.toList());
        reviewImageRepository.saveAll(reviewImages);
      }
    }

    Review updatedReview = reviewRepository.save(review);
    return reviewMapper.toDto(
        reviewRepository
            .findByIdWithRelations(updatedReview.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá")));
  }

  @Override
  @Transactional(readOnly = true)
  public ReviewResponseDTO getById(Integer id) {
    Review review =
        reviewRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại"));
    return reviewMapper.toDto(review);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReviewResponseDTO> getByProductId(
      Integer productId, Integer status, Pageable pageable) {
    Page<Review> reviews;
    if (status != null) {
      reviews = reviewRepository.findByProductIdAndStatusWithRelations(productId, status, pageable);
    } else {
      reviews = reviewRepository.findByProductIdWithRelations(productId, pageable);
    }
    return reviews.map(reviewMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReviewResponseDTO> getByCustomerId(Integer customerId, Pageable pageable) {
    Page<Review> reviews = reviewRepository.findByCustomerId(customerId, pageable);
    return reviews.map(
        review ->
            reviewMapper.toDto(
                reviewRepository
                    .findByIdWithRelations(review.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"))));
  }

  @Override
  public void delete(Integer id, Integer customerId) {
    Review review =
        reviewRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại"));

    if (customerId != null && !review.getCustomer().getId().equals(customerId)) {
      throw new UnauthorizedException("Bạn không có quyền xóa đánh giá này");
    }

    reviewRepository.delete(review);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductReviewSummaryDTO getProductReviewSummary(Integer productId) {
    Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
    Long totalReviews = reviewRepository.getReviewCountByProductId(productId);

    List<Review> reviews =
        reviewRepository.findByProductIdAndStatus(productId, 1, Pageable.unpaged()).getContent();
    Map<Integer, Long> ratingDistribution = new HashMap<>();
    for (int i = 1; i <= 5; i++) {
      ratingDistribution.put(i, 0L);
    }
    reviews.forEach(
        review -> {
          int rating = review.getRating();
          ratingDistribution.put(rating, ratingDistribution.get(rating) + 1);
        });

    return ProductReviewSummaryDTO.builder()
        .averageRating(averageRating != null ? averageRating : 0.0)
        .totalReviews(totalReviews != null ? totalReviews : 0L)
        .ratingDistribution(ratingDistribution)
        .build();
  }

  @Override
  public ReviewResponseDTO updateStatus(Integer id, Integer status) {
    Review review =
        reviewRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại"));

    review.setStatus(status);
    Review updatedReview = reviewRepository.save(review);
    return reviewMapper.toDto(
        reviewRepository
            .findByIdWithRelations(updatedReview.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá")));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReviewResponseDTO> getByOrderId(Integer orderId) {
    List<Review> reviews = reviewRepository.findByOrderIdWithRelations(orderId);
    return reviews.stream()
        .map(review -> {
          review.setReviewImages(reviewImageRepository.findByReviewId(review.getId()));
          return reviewMapper.toDto(review);
        })
        .collect(Collectors.toList());
  }
}
