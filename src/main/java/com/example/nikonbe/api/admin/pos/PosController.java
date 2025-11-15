package com.example.nikonbe.api.admin.pos;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.orders.dto.response.ListOrderResponse;
import com.example.nikonbe.modules.pos.dto.request.CompletePosOrderRequest;
import com.example.nikonbe.modules.pos.dto.request.CreatePosPendingOrderRequest;
import com.example.nikonbe.modules.pos.dto.request.UpdatePosPendingOrderRequest;
import com.example.nikonbe.modules.pos.dto.response.ListOrderPosResponse;
import com.example.nikonbe.modules.pos.dto.response.PosOrderResponse;
import com.example.nikonbe.modules.pos.dto.response.ProductDetailPosResponse;
import com.example.nikonbe.modules.pos.service.interF.PosService;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.admin.version}/pos")
@RequiredArgsConstructor
@Tag(name = "Admin - POS Management", description = "API bán hàng dành cho admin")
public class PosController {
  private final PosService posService;

  @GetMapping("/products")
  @Operation(
      summary = "Lấy danh sách sản phẩm với bộ lọc đầy đủ",
      description =
          "API lấy danh sách sản phẩm cho POS với các bộ lọc theo thương hiệu, danh mục, chất liệu, loại dây đeo và trạng thái")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách sản phẩm thành công")
  public ResponseEntity<ApiResponseDto<List<ProductResponseDTO>>> getProducts(
      @Parameter(description = "Từ khóa tìm kiếm theo tên và mô tả sản phẩm")
          @RequestParam(required = false)
          String keyword,
      @Parameter(description = "ID thương hiệu") @RequestParam(required = false) Integer brandId,
      @Parameter(description = "ID danh mục") @RequestParam(required = false) Integer categoryId,
      @Parameter(description = "ID chất liệu") @RequestParam(required = false) Integer materialId,
      @Parameter(description = "ID loại dây đeo") @RequestParam(required = false)
          Integer strapTypeId,
      @Parameter(description = "Trạng thái sản phẩm") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Số sản phẩm trên mỗi trang") @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ProductResponseDTO> result =
        posService.getProducts(
            keyword, brandId, categoryId, materialId, strapTypeId, status, pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách sản phẩm thành công");
  }

  @GetMapping("/products/{productId}/details")
  @Operation(
      summary = "Lấy danh sách chi tiết sản phẩm theo ID sản phẩm",
      description = "API lấy danh sách các biến thể (màu sắc, dung tích) của một sản phẩm cụ thể")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách chi tiết sản phẩm thành công")
  public ResponseEntity<ApiResponseDto<List<ProductDetailPosResponse>>> getProductDetails(
      @Parameter(description = "ID sản phẩm", required = true) @PathVariable Integer productId,
      @Parameter(description = "Mã SKU để tìm kiếm") @RequestParam(required = false) String sku,
      @Parameter(description = "ID màu sắc") @RequestParam(required = false) Integer colorId,
      @Parameter(description = "ID dung tích") @RequestParam(required = false) Integer capacityId,
      @Parameter(description = "Trạng thái chi tiết sản phẩm") @RequestParam(required = false)
          Status status,
      @Parameter(description = "Giá tối thiểu") @RequestParam(required = false) BigDecimal minPrice,
      @Parameter(description = "Giá tối đa") @RequestParam(required = false) BigDecimal maxPrice,
      @Parameter(description = "ID khuyến mãi") @RequestParam(required = false) Integer promotionId,
      @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Số chi tiết sản phẩm trên mỗi trang")
          @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ProductDetailPosResponse> result =
        posService.getProductDetailsByProductId(
            productId, sku, colorId, capacityId, status, minPrice, maxPrice, promotionId, pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách chi tiết sản phẩm thành công");
  }

  @PostMapping("/orders/pending")
  @Operation(
      summary = "Tạo đơn hàng POS chờ thanh toán",
      description = "Tạo đơn hàng trống với trạng thái PENDING_PAYMENT")
  @ApiResponse(responseCode = "200", description = "Tạo đơn hàng chờ thanh toán thành công")
  public ResponseEntity<ApiResponseDto<ListOrderResponse>> createPendingOrder(
      @RequestBody CreatePosPendingOrderRequest request) {
    ListOrderResponse response = posService.createPendingPOSOrder(request);
    return ResponseUtils.success(response, "Tạo đơn hàng chờ thanh toán thành công");
  }

  @GetMapping("/orders/pending")
  @Operation(
      summary = "Lấy danh sách đơn hàng POS chờ thanh toán",
      description = "Lấy danh sách đơn hàng với trạng thái PENDING_PAYMENT")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy danh sách đơn hàng chờ thanh toán thành công")
  public ResponseEntity<ApiResponseDto<List<ListOrderPosResponse>>> getPendingOrders(
      @Parameter(description = "ID khách hàng (tùy chọn)") @RequestParam(required = false)
          Integer customerId,
      @Parameter(description = "ID nhân viên (tùy chọn)") @RequestParam(required = false)
          Integer staffId,
      @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Số đơn hàng trên mỗi trang") @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ListOrderPosResponse> result =
        posService.getPendingPOSOrders(customerId, staffId, pageable);
    return ResponseUtils.successWithPage(
        result, "Lấy danh sách đơn hàng chờ thanh toán thành công");
  }

  @GetMapping("/orders/pending/{orderId}")
  @Operation(
      summary = "Lấy thông tin chi tiết đơn hàng POS chờ thanh toán",
      description = "API lấy thông tin chi tiết của một đơn hàng POS theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy thông tin đơn hàng thành công")
  public ResponseEntity<ApiResponseDto<PosOrderResponse>> getPendingOrderById(
      @Parameter(description = "ID đơn hàng", required = true) @PathVariable Integer orderId) {
    PosOrderResponse response = posService.getPendingOrderById(orderId);
    return ResponseUtils.success(response, "Lấy thông tin đơn hàng thành công");
  }

  @PutMapping("/orders/pending/{orderId}")
  @Operation(
      summary = "Cập nhật đơn hàng POS chờ thanh toán",
      description = "API cập nhật thông tin đơn hàng và chi tiết sản phẩm")
  @ApiResponse(responseCode = "200", description = "Cập nhật đơn hàng thành công")
  public ResponseEntity<ApiResponseDto<PosOrderResponse>> updatePendingOrder(
      @Parameter(description = "ID đơn hàng", required = true) @PathVariable Integer orderId,
      @RequestBody UpdatePosPendingOrderRequest request) {
    PosOrderResponse response = posService.updatePendingOrder(orderId, request);
    return ResponseUtils.success(response, "Cập nhật đơn hàng thành công");
  }

  @PostMapping("/orders/pending/{orderId}/complete")
  @Operation(
      summary = "Hoàn tất đơn hàng POS",
      description = "API hoàn tất đơn hàng và chuyển sang trạng thái COMPLETED")
  @ApiResponse(responseCode = "200", description = "Hoàn tất đơn hàng thành công")
  public ResponseEntity<ApiResponseDto<PosOrderResponse>> completeOrder(
      @Parameter(description = "ID đơn hàng", required = true) @PathVariable Integer orderId,
      @RequestBody CompletePosOrderRequest request) {
    PosOrderResponse response = posService.completeOrder(orderId, request);
    return ResponseUtils.success(response, "Hoàn tất đơn hàng thành công");
  }

  @DeleteMapping("/orders/pending/{orderId}")
  @Operation(
      summary = "Hủy đơn hàng POS chờ thanh toán",
      description = "API hủy đơn hàng POS và hoàn trả stock cho các sản phẩm")
  @ApiResponse(responseCode = "200", description = "Hủy đơn hàng thành công")
  public ResponseEntity<ApiResponseDto<PosOrderResponse>> cancelPendingOrder(
      @Parameter(description = "ID đơn hàng", required = true) @PathVariable Integer orderId,
      @Parameter(description = "ID nhân viên thực hiện hủy", required = true) @RequestParam
          Integer staffId,
      @Parameter(description = "Lý do hủy đơn hàng")
          @RequestParam(required = false, defaultValue = "Không có lý do")
          String cancelReason) {

    PosOrderResponse response = posService.cancelPendingOrder(orderId, staffId, cancelReason);
    return ResponseUtils.success(response, "Hủy đơn hàng thành công");
  }

  @GetMapping("/product-details/search")
  @Operation(
      summary = "Tìm kiếm product detail bằng SKU hoặc slug",
      description = "API tìm kiếm product detail theo SKU (dùng cho barcode/QR code)")
  @ApiResponse(responseCode = "200", description = "Tìm kiếm product detail thành công")
  public ResponseEntity<ApiResponseDto<ProductDetailPosResponse>> searchProductDetailBySlug(
      @Parameter(description = "Slug hoặc SKU để tìm kiếm") @RequestParam(required = false)
          String slug,
      @Parameter(description = "SKU để tìm kiếm") @RequestParam(required = false) String sku) {
    String searchValue = sku != null ? sku : slug;
    if (searchValue == null || searchValue.trim().isEmpty()) {
      return ResponseUtils.error(
          "SKU hoặc slug không được để trống", org.springframework.http.HttpStatus.BAD_REQUEST);
    }
    ProductDetailPosResponse response = posService.searchProductDetailBySlug(searchValue);
    return ResponseUtils.success(response, "Tìm kiếm product detail thành công");
  }
}
