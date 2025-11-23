package com.example.nikonbe.api.client.review;

import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.review.dto.request.ReviewCreateDTO;
import com.example.nikonbe.modules.review.dto.request.ReviewUpdateDTO;
import com.example.nikonbe.modules.review.dto.response.ProductReviewSummaryDTO;
import com.example.nikonbe.modules.review.dto.response.ReviewResponseDTO;
import com.example.nikonbe.modules.review.service.interF.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/reviews")
@RequiredArgsConstructor
@Tag(name = "Client - Review")
public class ReviewController {

  private final ReviewService reviewService;
  private final CustomerRepository customerRepository;

  private Integer getCustomerIdFromAuth(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ValidationException("Người dùng chưa đăng nhập");
    }
    String username = authentication.getName();
    Customer customer =
        customerRepository
            .findByEmailOrUsername(username)
            .orElseThrow(() -> new ValidationException("Không tìm thấy khách hàng"));
    return customer.getId();
  }

  @PostMapping
  @Operation(
      summary = "Tạo đánh giá sản phẩm",
      description = "Khách hàng tạo đánh giá cho sản phẩm")
  public ResponseEntity<ApiResponseDto<ReviewResponseDTO>> create(
      @Valid @RequestBody ReviewCreateDTO dto, Authentication authentication) {
    Integer customerId = getCustomerIdFromAuth(authentication);
    ReviewResponseDTO result = reviewService.create(dto, customerId);
    return ResponseUtils.success(result, "Tạo đánh giá thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật đánh giá", description = "Khách hàng cập nhật đánh giá của mình")
  public ResponseEntity<ApiResponseDto<ReviewResponseDTO>> update(
      @PathVariable Integer id,
      @Valid @RequestBody ReviewUpdateDTO dto,
      Authentication authentication) {
    Integer customerId = getCustomerIdFromAuth(authentication);
    ReviewResponseDTO result = reviewService.update(id, dto, customerId);
    return ResponseUtils.success(result, "Cập nhật đánh giá thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết đánh giá", description = "Lấy thông tin chi tiết một đánh giá")
  public ResponseEntity<ApiResponseDto<ReviewResponseDTO>> getById(
      @Parameter(description = "ID đánh giá") @PathVariable Integer id) {
    ReviewResponseDTO result = reviewService.getById(id);
    return ResponseUtils.success(result, "Lấy đánh giá thành công");
  }

  @GetMapping("/product/{productId}")
  @Operation(
      summary = "Lấy danh sách đánh giá theo sản phẩm",
      description = "Lấy danh sách đánh giá của một sản phẩm")
  public ResponseEntity<ApiResponseDto<java.util.List<ReviewResponseDTO>>> getByProductId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Parameter(description = "Trạng thái (1: Approved, 0: Pending, -1: Rejected)")
          @RequestParam(required = false)
          Integer status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ReviewResponseDTO> result = reviewService.getByProductId(productId, status, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách đánh giá thành công");
  }

  @GetMapping("/product/{productId}/summary")
  @Operation(
      summary = "Lấy tổng hợp đánh giá sản phẩm",
      description = "Lấy thống kê đánh giá (rating trung bình, tổng số đánh giá, phân bố rating)")
  public ResponseEntity<ApiResponseDto<ProductReviewSummaryDTO>> getProductReviewSummary(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    ProductReviewSummaryDTO result = reviewService.getProductReviewSummary(productId);
    return ResponseUtils.success(result, "Lấy tổng hợp đánh giá thành công");
  }

  @GetMapping("/my-reviews")
  @Operation(
      summary = "Lấy danh sách đánh giá của tôi",
      description = "Lấy danh sách đánh giá của khách hàng hiện tại")
  public ResponseEntity<ApiResponseDto<java.util.List<ReviewResponseDTO>>> getMyReviews(
      Authentication authentication,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Integer customerId = getCustomerIdFromAuth(authentication);
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ReviewResponseDTO> result = reviewService.getByCustomerId(customerId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách đánh giá thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa đánh giá", description = "Khách hàng xóa đánh giá của mình")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @PathVariable Integer id, Authentication authentication) {
    Integer customerId = getCustomerIdFromAuth(authentication);
    reviewService.delete(id, customerId);
    return ResponseUtils.success(null, "Xóa đánh giá thành công");
  }
}
