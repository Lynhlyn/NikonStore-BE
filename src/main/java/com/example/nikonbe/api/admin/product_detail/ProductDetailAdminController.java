package com.example.nikonbe.api.admin.product_detail;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailCreateDTO;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailUpdateDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.service.impl.ProductDetailExcelExportService;
import com.example.nikonbe.modules.product_detail.service.interF.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/product-details")
@Tag(name = "Admin - Product Detail Management")
public class ProductDetailAdminController {

  private final ProductDetailService service;
  private final ProductDetailExcelExportService excelExportService;

  public ProductDetailAdminController(
      ProductDetailService service, ProductDetailExcelExportService excelExportService) {
    this.service = service;
    this.excelExportService = excelExportService;
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
      @RequestParam(required = false) String sku,
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
        service.getAll(sku, status, productId, colorId, capacityId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách biến thể thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa biến thể")
  public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Xóa biến thể thành công");
  }

  @GetMapping("/export/excel")
  @Operation(summary = "Xuất Excel danh sách sản phẩm chi tiết")
  public ResponseEntity<byte[]> exportProductDetailsToExcel(
      @RequestParam(required = false) String sku,
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) Integer productId,
      @RequestParam(required = false) Integer colorId,
      @RequestParam(required = false) Integer capacityId) {
    try {
      byte[] excelData =
          excelExportService.exportProductDetailsToExcel(
              sku, status, productId, colorId, capacityId);

      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      String filename = "DanhSachSanPhamChiTiet_" + timestamp + ".xlsx";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", filename);
      headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

      return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

