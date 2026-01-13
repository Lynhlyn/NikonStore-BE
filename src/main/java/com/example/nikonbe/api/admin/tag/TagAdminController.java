package com.example.nikonbe.api.admin.tag;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.tag.dto.request.TagCreateDTO;
import com.example.nikonbe.modules.tag.dto.request.TagUpdateDTO;
import com.example.nikonbe.modules.tag.dto.response.TagResponseDTO;
import com.example.nikonbe.modules.tag.service.interF.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("${api.admin.version}/tags")
@RequiredArgsConstructor
@Tag(name = "Admin - Tag Management", description = "API quản lý thẻ dành cho admin")
public class TagAdminController {

  private final TagService tagService;

  @PostMapping
  @Operation(summary = "Tạo mới thẻ", description = "Tạo một thẻ mới với thông tin được cung cấp")
  @ApiResponse(responseCode = "201", description = "Tạo thành công")
  @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
  @ApiResponse(responseCode = "409", description = "Thẻ đã tồn tại")
  public ResponseEntity<ApiResponseDto<TagResponseDTO>> create(
      @Valid @RequestBody TagCreateDTO dto) {
    TagResponseDTO result = tagService.create(dto);
    return ResponseUtils.success(result, "Tag created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật thẻ", description = "Cập nhật thông tin của một thẻ hiện có")
  @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
  @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy thẻ")
  @ApiResponse(responseCode = "409", description = "Thẻ đã tồn tại")
  public ResponseEntity<ApiResponseDto<TagResponseDTO>> update(
      @Parameter(description = "ID thẻ", example = "1") @PathVariable Integer id,
      @Valid @RequestBody TagUpdateDTO dto) {
    TagResponseDTO result = tagService.update(id, dto);
    return ResponseUtils.success(result, "Tag updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy thẻ theo ID",
      description = "Lấy thông tin chi tiết của một thẻ theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy thẻ")
  public ResponseEntity<ApiResponseDto<TagResponseDTO>> getById(
      @Parameter(description = "ID thẻ", example = "1") @PathVariable Integer id) {
    TagResponseDTO result = tagService.getById(id);
    return ResponseUtils.success(result, "Tag retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách thẻ",
      description = "Hỗ trợ phân trang và tìm kiếm theo tên, slug và trạng thái")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<TagResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang", example = "false")
          @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên", example = "Technology")
          @RequestParam(required = false)
          String name,
      @Parameter(description = "Tìm kiếm theo slug", example = "technology")
          @RequestParam(required = false)
          String slug,
      @Parameter(
              description = "Lọc theo trạng thái",
              schema = @Schema(implementation = Status.class))
          @RequestParam(required = false)
          Status status,
      @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Kích thước trang", example = "10")
          @RequestParam(defaultValue = "10")
          int size,
      @Parameter(description = "Sắp xếp theo trường", example = "name")
          @RequestParam(defaultValue = "name")
          String sort,
      @Parameter(description = "Hướng sắp xếp (asc/desc)", example = "desc")
          @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<TagResponseDTO> result = tagService.getAll(name, slug, status);
      return ResponseUtils.success(result, "Tags retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<TagResponseDTO> result = tagService.getAllPaginated(name, slug, status, pageable);
    return ResponseUtils.successWithPage(result, "Tags retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Xóa thẻ",
      description = "Xóa một thẻ bằng cách chuyển trạng thái sang DELETED")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy thẻ")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID thẻ", example = "1") @PathVariable Integer id) {
    tagService.delete(id);
    return ResponseUtils.success(null, "Tag deleted successfully");
  }
}
