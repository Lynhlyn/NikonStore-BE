package com.example.nikonbe.api.admin.product_detail;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailCreateDTO;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailUpdateDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.service.interF.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/v1/product-details")
@Tag(name = "Admin - Product Detail Management")
public class ProductDetailAdminController {

  private final ProductDetailService service;

  public ProductDetailAdminController(ProductDetailService service) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Tạo biến thể sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductDetailResponseDTO>> create(
      @Valid @RequestBody ProductDetailCreateDTO dto) {
    ProductDetailResponseDTO result = service.create(dto);
    return ResponseUtils.success(result, "Tạo biến thể thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật biến thể sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductDetailResponseDTO>> update(
      @PathVariable Integer id, @Valid @RequestBody ProductDetailUpdateDTO dto) {
    ProductDetailResponseDTO result = service.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật biến thể thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết biến thể")
  public ResponseEntity<ApiResponseDto<ProductDetailResponseDTO>> getById(
      @PathVariable Integer id) {
    ProductDetailResponseDTO result = service.getById(id);
    return ResponseUtils.success(result, "Lấy biến thể thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách biến thể")
  public ResponseEntity<ApiResponseDto<java.util.List<ProductDetailResponseDTO>>> getAll(
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Integer productId,
      @RequestParam(required = false) Integer colorId,
      @RequestParam(required = false) Integer capacityId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "desc") String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ProductDetailResponseDTO> result =
        service.getAll(status, productId, colorId, capacityId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách biến thể thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa biến thể")
  public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Xóa biến thể thành công");
  }
}
