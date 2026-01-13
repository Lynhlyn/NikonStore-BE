package com.example.nikonbe.api.admin.content_category;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryCreateDTO;
import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryUpdateDTO;
import com.example.nikonbe.modules.content_category.dto.response.ContentCategoryResponseDTO;
import com.example.nikonbe.modules.content_category.service.interF.ContentCategoryService;
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
@RequestMapping("${api.admin.version}/content-categories")
@RequiredArgsConstructor
@Tag(
    name = "Admin - Content Category Management",
    description = "Các API quản lý danh mục nội dung cho admin")
public class ContentCategoryAdminController {

  private final ContentCategoryService service;

  @PostMapping
  @Operation(summary = "Tạo mới danh mục nội dung")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<ContentCategoryResponseDTO>> create(
      @Valid @RequestBody ContentCategoryCreateDTO dto) {
    ContentCategoryResponseDTO result = service.create(dto);
    return ResponseUtils.success(
        result, "Content category created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật danh mục nội dung")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
  })
  public ResponseEntity<ApiResponseDto<ContentCategoryResponseDTO>> update(
      @Parameter(description = "ID danh mục") @PathVariable Integer id,
      @Valid @RequestBody ContentCategoryUpdateDTO dto) {
    ContentCategoryResponseDTO result = service.update(id, dto);
    return ResponseUtils.success(result, "Content category updated successfully");
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
  public ResponseEntity<ApiResponseDto<ContentCategoryResponseDTO>> getById(
      @Parameter(description = "ID danh mục") @PathVariable Integer id) {
    ContentCategoryResponseDTO result = service.getById(id);
    return ResponseUtils.success(result, "Content category retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách danh mục nội dung",
      description = "Có thể lấy tất cả hoặc phân trang, tìm kiếm theo tên hoặc slug hoặc type")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<ContentCategoryResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String name,
      @Parameter(description = "Tìm kiếm theo slug") @RequestParam(required = false) String slug,
      @Parameter(description = "Tìm kiếm theo type") @RequestParam(required = false) String type,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<ContentCategoryResponseDTO> result = service.getAll(name, slug, type);

      return ResponseUtils.success(result, "Content categories retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ContentCategoryResponseDTO> result = service.getAllPaginated(name, slug, type, pageable);

    return ResponseUtils.successWithPage(result, "Content categories retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa danh mục", description = "Xóa cứng danh mục khỏi hệ thống")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID danh mục") @PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Content category deleted successfully");
  }
}
