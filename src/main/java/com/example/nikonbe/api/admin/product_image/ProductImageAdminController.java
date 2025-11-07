package com.example.nikonbe.api.admin.product_image;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageCreateDTO;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageUpdateDTO;
import com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO;
import com.example.nikonbe.modules.product_image.service.interF.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/product-images")
@RequiredArgsConstructor
@Tag(name = "Admin - Product Image Management")
public class ProductImageAdminController {

  private final ProductImageService service;

  @PostMapping
  @Operation(summary = "Tạo hình ảnh sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductImageResponseDTO>> create(
      @Valid @RequestBody ProductImageCreateDTO dto) {
    ProductImageResponseDTO result = service.create(dto);
    return ResponseUtils.success(result, "Tạo hình ảnh sản phẩm thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật hình ảnh sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductImageResponseDTO>> update(
      @PathVariable Integer id, @Valid @RequestBody ProductImageUpdateDTO dto) {
    ProductImageResponseDTO result = service.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật hình ảnh sản phẩm thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết hình ảnh sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductImageResponseDTO>> getById(@PathVariable Integer id) {
    ProductImageResponseDTO result = service.getById(id);
    return ResponseUtils.success(result, "Lấy hình ảnh sản phẩm thành công");
  }

  @GetMapping("/product/{productId}")
  @Operation(summary = "Lấy danh sách hình ảnh của sản phẩm")
  public ResponseEntity<ApiResponseDto<List<ProductImageResponseDTO>>> getByProductId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    List<ProductImageResponseDTO> result = service.getByProductId(productId);
    return ResponseUtils.success(result, "Lấy danh sách hình ảnh sản phẩm thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa hình ảnh sản phẩm")
  public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Xóa hình ảnh sản phẩm thành công");
  }

  @DeleteMapping("/product/{productId}")
  @Operation(summary = "Xóa tất cả hình ảnh của sản phẩm")
  public ResponseEntity<ApiResponseDto<Void>> deleteByProductId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    service.deleteByProductId(productId);
    return ResponseUtils.success(null, "Xóa tất cả hình ảnh sản phẩm thành công");
  }
}
