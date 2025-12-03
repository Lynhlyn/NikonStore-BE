package com.example.nikonbe.api.client.blog;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.blog.dto.response.BlogResponseDTO;
import com.example.nikonbe.modules.blog.service.interF.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/blogs")
@RequiredArgsConstructor
@Tag(name = "Client - Blog")
public class BlogController {

  private final BlogService blogService;

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết blog", description = "Lấy thông tin chi tiết một blog và tăng view count")
  public ResponseEntity<ApiResponseDto<BlogResponseDTO>> getById(
      @Parameter(description = "ID blog") @PathVariable Integer id) {
    blogService.incrementViewCount(id);
    BlogResponseDTO result = blogService.getById(id);
    return ResponseUtils.success(result, "Lấy blog thành công");
  }

  @GetMapping("/slug/{slug}")
  @Operation(summary = "Lấy chi tiết blog theo slug", description = "Lấy thông tin chi tiết một blog theo slug và tăng view count")
  public ResponseEntity<ApiResponseDto<BlogResponseDTO>> getBySlug(
      @Parameter(description = "Slug blog") @PathVariable String slug) {
    BlogResponseDTO result = blogService.getBySlug(slug);
    blogService.incrementViewCount(result.getId());
    return ResponseUtils.success(result, "Lấy blog thành công");
  }

  @GetMapping
  @Operation(
      summary = "Danh sách blog",
      description = "Lấy danh sách blog đã xuất bản với bộ lọc")
  public ResponseEntity<ApiResponseDto<java.util.List<BlogResponseDTO>>> getAll(
      @Parameter(description = "ID danh mục") @RequestParam(required = false) Integer categoryId,
      @Parameter(description = "ID tag") @RequestParam(required = false) Integer tagId,
      @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<BlogResponseDTO> result =
        blogService.getAll(categoryId, tagId, null, true, keyword, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách blog thành công");
  }
}


