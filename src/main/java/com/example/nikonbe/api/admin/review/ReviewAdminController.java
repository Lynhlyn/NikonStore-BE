package com.example.nikonbe.api.admin.review;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.review.dto.response.ProductReviewSummaryDTO;
import com.example.nikonbe.modules.review.dto.response.ReviewResponseDTO;
import com.example.nikonbe.modules.review.service.interF.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/reviews")
@Tag(name = "Admin - Review Management")
public class ReviewAdminController {

  private final ReviewService reviewService;

  public ReviewAdminController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết đánh giá")
  public ResponseEntity<ApiResponseDto<ReviewResponseDTO>> getById(
      @Parameter(description = "ID đánh giá") @PathVariable Integer id) {
    ReviewResponseDTO result = reviewService.getById(id);
    return ResponseUtils.success(result, "Lấy đánh giá thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách đánh giá", description = "Lấy danh sách đánh giá với bộ lọc")
  public ResponseEntity<ApiResponseDto<java.util.List<ReviewResponseDTO>>> getAll(
      @Parameter(description = "ID sản phẩm") @RequestParam(required = false) Integer productId,
      @Parameter(description = "ID khách hàng") @RequestParam(required = false) Integer customerId,
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
    Page<ReviewResponseDTO> result;
    if (productId != null) {
      result = reviewService.getByProductId(productId, status, pageable);
    } else if (customerId != null) {
      result = reviewService.getByCustomerId(customerId, pageable);
    } else {
      result = Page.empty(pageable);
    }
    return ResponseUtils.successWithPage(result, "Lấy danh sách đánh giá thành công");
  }

  @PutMapping("/{id}/status")
  @Operation(
      summary = "Cập nhật trạng thái đánh giá",
      description = "Admin duyệt hoặc từ chối đánh giá")
  public ResponseEntity<ApiResponseDto<ReviewResponseDTO>> updateStatus(
      @Parameter(description = "ID đánh giá") @PathVariable Integer id,
      @Parameter(description = "Trạng thái (1: Approved, 0: Pending, -1: Rejected)") @RequestParam
          Integer status) {
    ReviewResponseDTO result = reviewService.updateStatus(id, status);
    return ResponseUtils.success(result, "Cập nhật trạng thái đánh giá thành công");
  }

  @GetMapping("/product/{productId}/summary")
  @Operation(
      summary = "Lấy tổng hợp đánh giá sản phẩm",
      description = "Lấy thống kê đánh giá của một sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductReviewSummaryDTO>> getProductReviewSummary(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    ProductReviewSummaryDTO result = reviewService.getProductReviewSummary(productId);
    return ResponseUtils.success(result, "Lấy tổng hợp đánh giá thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa đánh giá", description = "Admin xóa đánh giá")
  public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Integer id) {
    reviewService.delete(id, null);
    return ResponseUtils.success(null, "Xóa đánh giá thành công");
  }

  @GetMapping("/order/{orderId}")
  @Operation(
      summary = "Lấy danh sách đánh giá theo đơn hàng",
      description = "Lấy tất cả đánh giá của các sản phẩm trong một đơn hàng")
  public ResponseEntity<ApiResponseDto<java.util.List<ReviewResponseDTO>>> getByOrderId(
      @Parameter(description = "ID đơn hàng") @PathVariable Integer orderId) {
    java.util.List<ReviewResponseDTO> result = reviewService.getByOrderId(orderId);
    return ResponseUtils.success(result, "Lấy danh sách đánh giá thành công");
  }
}
