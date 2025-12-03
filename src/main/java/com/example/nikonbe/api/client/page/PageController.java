package com.example.nikonbe.api.client.page;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.page.dto.response.PageDto;
import com.example.nikonbe.modules.page.service.interF.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.client.version}/page")
@RequiredArgsConstructor
@Tag(name = "Client - Page", description = "API lấy nội dung trang cho client")
public class PageController {

  private final PageService pageService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách tất cả pages",
      description = "Lấy danh sách tất cả các trang có sẵn cho client")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách pages thành công")
  public ResponseEntity<ApiResponseDto<List<PageDto>>> getAllPages() {
    List<PageDto> result = pageService.getAllForClient();
    return ResponseUtils.success(result, "Lấy danh sách pages thành công");
  }

  @GetMapping("/{slug}")
  @Operation(
      summary = "Lấy page theo slug",
      description = "Lấy thông tin trang theo slug. Trả về object rỗng nếu không tìm thấy.")
  @ApiResponse(responseCode = "200", description = "Lấy page thành công")
  public ResponseEntity<ApiResponseDto<PageDto>> getPageBySlug(
      @Parameter(description = "Slug của page") @PathVariable String slug) {

    PageDto result = pageService.getBySlugForClient(slug);
    return ResponseUtils.success(result, "Lấy page thành công");
  }
}
