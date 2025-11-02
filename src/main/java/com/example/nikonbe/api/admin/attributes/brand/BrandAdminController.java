package com.example.nikonbe.api.admin.attributes.brand;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandCreateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandUpdateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.brand.service.interF.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/brands")
@RequiredArgsConstructor
@Tag(name = "Admin - Brand Management", description = "Các API quản lý thương hiệu cho admin")
public class BrandAdminController {

  private final BrandService brandService;

  @PostMapping
  @Operation(summary = "Tạo mới thương hiệu")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<BrandResponseDTO>> create(
      @Valid @RequestBody BrandCreateDTO dto) {
    BrandResponseDTO result = brandService.create(dto);
    return ResponseUtils.success(result, "Brand created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật thương hiệu")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu")
  })
  public ResponseEntity<ApiResponseDto<BrandResponseDTO>> update(
      @Parameter(description = "ID thương hiệu") @PathVariable Integer id,
      @Valid @RequestBody BrandUpdateDTO dto) {
    BrandResponseDTO result = brandService.update(id, dto);
    return ResponseUtils.success(result, "Brand updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy thương hiệu theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu")
  })
  public ResponseEntity<ApiResponseDto<BrandResponseDTO>> getById(
      @Parameter(description = "ID thương hiệu") @PathVariable Integer id) {
    BrandResponseDTO result = brandService.getById(id);
    return ResponseUtils.success(result, "Brand retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách thương hiệu",
      description = "Có thể lấy tất cả hoặc phân trang. Có thể lọc theo trạng thái.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<BrandResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<BrandResponseDTO> result =
          status != null ? brandService.getAllByStatus(status) : brandService.getAll();
      return ResponseUtils.success(result, "Brands retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<BrandResponseDTO> result =
        status != null
            ? brandService.getAllByStatusPaginated(status, pageable)
            : brandService.getAllPaginated(pageable);

    return ResponseUtils.successWithPage(result, "Brands retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa thương hiệu", description = "Đánh dấu thương hiệu là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID thương hiệu") @PathVariable Integer id) {
    brandService.delete(id);
    return ResponseUtils.success(null, "Brand deleted successfully");
  }
}
