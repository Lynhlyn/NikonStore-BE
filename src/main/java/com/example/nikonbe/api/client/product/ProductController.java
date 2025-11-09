package com.example.nikonbe.api.client.product;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.service.interF.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/v1/products")
@Tag(name = "Client - Product")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductResponseDTO>> getById(@PathVariable Integer id) {
    return ResponseUtils.success(service.getById(id), "Lấy sản phẩm thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách sản phẩm")
  public ResponseEntity<ApiResponseDto<java.util.List<ProductResponseDTO>>> getAll(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Integer brandId,
      @RequestParam(required = false) Integer materialId,
      @RequestParam(required = false) Integer strapTypeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "12") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "desc") String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ProductResponseDTO> result =
        service.getAll(keyword, status, categoryId, brandId, materialId, strapTypeId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách sản phẩm thành công");
  }
}
