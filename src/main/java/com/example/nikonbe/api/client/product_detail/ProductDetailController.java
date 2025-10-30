package com.example.nikonbe.api.client.product_detail;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.service.interF.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/v1/product-details")
@Tag(name = "Client - Product Detail")
public class ProductDetailController {

  private final ProductDetailService service;

  public ProductDetailController(ProductDetailService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết biến thể")
  public ResponseEntity<ApiResponseDto<ProductDetailResponseDTO>> getById(
      @PathVariable Integer id) {
    return ResponseUtils.success(service.getById(id), "Lấy biến thể thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách biến thể")
  public ResponseEntity<ApiResponseDto<java.util.List<ProductDetailResponseDTO>>> getAll(
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Integer productId,
      @RequestParam(required = false) Integer colorId,
      @RequestParam(required = false) Integer capacityId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "12") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "desc") String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ProductDetailResponseDTO> result =
        service.getAll(status, productId, colorId, capacityId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách biến thể thành công");
  }
}
