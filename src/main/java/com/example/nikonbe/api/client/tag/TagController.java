package com.example.nikonbe.api.client.tag;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.tag.dto.response.TagResponseDTO;
import com.example.nikonbe.modules.tag.service.interF.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/tags")
@RequiredArgsConstructor
@Tag(name = "Client - Tag API", description = "API thẻ dành cho người dùng cuối")
public class TagController {

  private final TagService tagService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách thẻ đang hoạt động",
      description = "Lấy tất cả thẻ đang hiển thị (trạng thái ACTIVE) với bộ lọc tùy chọn")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<TagResponseDTO>>> getAll(
      @Parameter(description = "Tìm kiếm theo tên", example = "Technology")
          @RequestParam(required = false)
          String name,
      @Parameter(description = "Tìm kiếm theo slug", example = "technology")
          @RequestParam(required = false)
          String slug) {
    List<TagResponseDTO> result = tagService.getAll(name, slug, Status.ACTIVE);
    return ResponseUtils.success(result, "Tags retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy thông tin thẻ theo ID",
      description = "Lấy thông tin chi tiết của một thẻ theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy thẻ")
  public ResponseEntity<ApiResponseDto<TagResponseDTO>> getById(
      @Parameter(description = "ID thẻ", example = "1") @PathVariable Integer id) {
    TagResponseDTO result = tagService.getById(id);
    return ResponseUtils.success(result, "Tag retrieved successfully");
  }
}
