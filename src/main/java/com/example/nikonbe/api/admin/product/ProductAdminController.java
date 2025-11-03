package com.example.nikonbe.api.admin.product;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.service.interF.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Tag(name = "Admin - Product Management")
public class ProductAdminController {

  private final ProductService service;

  public ProductAdminController(ProductService service) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Tạo sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductResponseDTO>> create(
      @Valid @RequestBody ProductCreateDTO dto) {
    ProductResponseDTO result = service.create(dto);
    return ResponseUtils.success(result, "Tạo sản phẩm thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductResponseDTO>> update(
      @PathVariable Integer id, @Valid @RequestBody ProductUpdateDTO dto) {
    ProductResponseDTO result = service.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật sản phẩm thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductResponseDTO>> getById(@PathVariable Integer id) {
    ProductResponseDTO result = service.getById(id);
    return ResponseUtils.success(result, "Lấy sản phẩm thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách sản phẩm")
  public ResponseEntity<ApiResponseDto<java.util.List<ProductResponseDTO>>> getAll(
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Integer brandId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "desc") String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ProductResponseDTO> result = service.getAll(status, categoryId, brandId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách sản phẩm thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa sản phẩm")
  public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Xóa sản phẩm thành công");
  }
}
