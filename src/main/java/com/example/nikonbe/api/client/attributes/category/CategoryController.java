package com.example.nikonbe.api.client.attributes.category;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.category.service.interF.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Client - Category API", description = "Các API danh mục dành cho người dùng")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách danh mục ACTIVE",
      description = "Có thể lấy tất cả hoặc phân trang")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CategoryResponseDTO>>> getActiveCategories(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<CategoryResponseDTO> result = categoryService.getAllByStatus(Status.ACTIVE);
      return ResponseUtils.success(result, "Categories retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CategoryResponseDTO> result =
        categoryService.getAllByStatusPaginated(Status.ACTIVE, pageable);

    return ResponseUtils.successWithPage(result, "Categories retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy danh mục ACTIVE theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục ACTIVE")
  })
  public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> getActiveById(
      @Parameter(description = "ID danh mục") @PathVariable Integer id) {
    CategoryResponseDTO category = categoryService.getById(id);

    if (category.getStatus() != Status.ACTIVE) {
      return ResponseUtils.error("Category not found", HttpStatus.NOT_FOUND);
    }

    return ResponseUtils.success(category, "Category retrieved successfully");
  }
}
