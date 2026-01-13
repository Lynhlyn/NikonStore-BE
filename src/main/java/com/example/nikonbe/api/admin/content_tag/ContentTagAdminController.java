package com.example.nikonbe.api.admin.content_tag;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.content_tag.dto.request.ContentTagCreateDTO;
import com.example.nikonbe.modules.content_tag.dto.request.ContentTagUpdateDTO;
import com.example.nikonbe.modules.content_tag.dto.response.ContentTagResponseDTO;
import com.example.nikonbe.modules.content_tag.service.interF.ContentTagService;
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
@RequestMapping("${api.admin.version}/content-tags")
@RequiredArgsConstructor
@Tag(name = "Admin - Content Tag Management", description = "Các API quản lý content tag cho admin")
public class ContentTagAdminController {

  private final ContentTagService service;

  @PostMapping
  @Operation(summary = "Tạo mới content tag")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<ContentTagResponseDTO>> create(
      @Valid @RequestBody ContentTagCreateDTO dto) {
    ContentTagResponseDTO result = service.create(dto);
    return ResponseUtils.success(result, "Content tag created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật content tag")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tag")
  })
  public ResponseEntity<ApiResponseDto<ContentTagResponseDTO>> update(
      @Parameter(description = "ID tag") @PathVariable Integer id,
      @Valid @RequestBody ContentTagUpdateDTO dto) {
    ContentTagResponseDTO result = service.update(id, dto);
    return ResponseUtils.success(result, "Content tag updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy tag theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tag")
  })
  public ResponseEntity<ApiResponseDto<ContentTagResponseDTO>> getById(
      @Parameter(description = "ID tag") @PathVariable Integer id) {
    ContentTagResponseDTO result = service.getById(id);
    return ResponseUtils.success(result, "Content tag retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách content tag",
      description = "Có thể lấy tất cả hoặc phân trang, tìm kiếm theo tên, slug hoặc type.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<ContentTagResponseDTO>>> getAll(
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
      List<ContentTagResponseDTO> result = service.getAll(name, slug, type);
      return ResponseUtils.success(result, "Content tags retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ContentTagResponseDTO> result = service.getAllPaginated(name, slug, type, pageable);
    return ResponseUtils.successWithPage(result, "Content tags retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa tag", description = "Xóa tag khỏi hệ thống")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tag")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID tag") @PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Content tag deleted successfully");
  }
}
