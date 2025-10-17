package com.example.nikonbe.api.admin.attributes.category;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryCreateDTO;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryUpdateDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.category.service.interF.CategoryService;
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
@RequestMapping("/api/admin/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Admin - Category Management", description = "Các API quản lý danh mục cho admin")
public class CategoryAdminController {

  private final CategoryService categoryService;

  @PostMapping
  @Operation(summary = "Tạo mới danh mục")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> create(
      @Valid @RequestBody CategoryCreateDTO dto) {
    CategoryResponseDTO result = categoryService.create(dto);
    return ResponseUtils.success(result, "Category created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật danh mục")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
  })
  public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> update(
      @Parameter(description = "ID danh mục") @PathVariable Integer id,
      @Valid @RequestBody CategoryUpdateDTO dto) {
    CategoryResponseDTO result = categoryService.update(id, dto);
    return ResponseUtils.success(result, "Category updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy danh mục theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
  })
  public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> getById(
      @Parameter(description = "ID danh mục") @PathVariable Integer id) {
    CategoryResponseDTO result = categoryService.getById(id);
    return ResponseUtils.success(result, "Category retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách danh mục",
      description = "Có thể lấy tất cả hoặc phân trang. Có thể lọc theo trạng thái hoặc tìm kiếm.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CategoryResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<CategoryResponseDTO> result =
          status != null ? categoryService.getAllByStatus(status) : categoryService.getAll();
      return ResponseUtils.success(result, "Categories retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CategoryResponseDTO> result;
    if (keyword != null && !keyword.isBlank()) {
      result = categoryService.search(keyword, status, pageable);
    } else if (status != null) {
      result = categoryService.getAllByStatusPaginated(status, pageable);
    } else {
      result = categoryService.getAllPaginated(pageable);
    }

    return ResponseUtils.successWithPage(result, "Categories retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa danh mục", description = "Đánh dấu danh mục là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID danh mục") @PathVariable Integer id) {
    categoryService.delete(id);
    return ResponseUtils.success(null, "Category deleted successfully");
  }
}
