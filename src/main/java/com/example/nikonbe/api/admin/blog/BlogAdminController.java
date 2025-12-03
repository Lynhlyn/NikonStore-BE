package com.example.nikonbe.api.admin.blog;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.blog.dto.request.BlogCreateDTO;
import com.example.nikonbe.modules.blog.dto.request.BlogUpdateDTO;
import com.example.nikonbe.modules.blog.dto.response.BlogResponseDTO;
import com.example.nikonbe.modules.blog.service.interF.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/blogs")
@RequiredArgsConstructor
@Tag(name = "Admin - Blog Management")
public class BlogAdminController {

  private final BlogService blogService;

  @PostMapping
  @Operation(summary = "Tạo blog mới", description = "Admin tạo blog mới")
  public ResponseEntity<ApiResponseDto<BlogResponseDTO>> create(
      @Valid @RequestBody BlogCreateDTO dto) {
    BlogResponseDTO result = blogService.create(dto);
    return ResponseUtils.success(result, "Tạo blog thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật blog", description = "Admin cập nhật blog")
  public ResponseEntity<ApiResponseDto<BlogResponseDTO>> update(
      @Parameter(description = "ID blog") @PathVariable Integer id,
      @Valid @RequestBody BlogUpdateDTO dto) {
    BlogResponseDTO result = blogService.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật blog thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết blog", description = "Lấy thông tin chi tiết một blog")
  public ResponseEntity<ApiResponseDto<BlogResponseDTO>> getById(
      @Parameter(description = "ID blog") @PathVariable Integer id) {
    BlogResponseDTO result = blogService.getById(id);
    return ResponseUtils.success(result, "Lấy blog thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách blog", description = "Lấy danh sách blog với bộ lọc")
  public ResponseEntity<ApiResponseDto<java.util.List<BlogResponseDTO>>> getAll(
      @Parameter(description = "ID danh mục") @RequestParam(required = false) Integer categoryId,
      @Parameter(description = "ID tag") @RequestParam(required = false) Integer tagId,
      @Parameter(description = "ID staff") @RequestParam(required = false) Integer staffId,
      @Parameter(description = "Trạng thái xuất bản") @RequestParam(required = false)
          Boolean isPublished,
      @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<BlogResponseDTO> result =
        blogService.getAll(categoryId, tagId, staffId, isPublished, keyword, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách blog thành công");
  }

  @PutMapping("/{id}/publish")
  @Operation(summary = "Cập nhật trạng thái xuất bản", description = "Admin cập nhật trạng thái xuất bản blog")
  public ResponseEntity<ApiResponseDto<BlogResponseDTO>> updatePublishStatus(
      @Parameter(description = "ID blog") @PathVariable Integer id,
      @Parameter(description = "Trạng thái xuất bản") @RequestParam Boolean isPublished) {
    BlogResponseDTO result = blogService.updatePublishStatus(id, isPublished);
    return ResponseUtils.success(result, "Cập nhật trạng thái xuất bản thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa blog", description = "Admin xóa blog")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID blog") @PathVariable Integer id) {
    blogService.delete(id);
    return ResponseUtils.success(null, "Xóa blog thành công");
  }
}


