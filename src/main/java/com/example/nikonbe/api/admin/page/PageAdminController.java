package com.example.nikonbe.api.admin.page;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.page.dto.request.PageCreateDto;
import com.example.nikonbe.modules.page.dto.request.PageUpdateDto;
import com.example.nikonbe.modules.page.dto.response.PageAdminDto;
import com.example.nikonbe.modules.page.service.interF.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/page")
@RequiredArgsConstructor
@Tag(name = "Admin - Page Management", description = "Các API quản lý page cho admin")
public class PageAdminController {

  private final PageService pageService;

  @GetMapping("/{pageKey}")
  @Operation(
      summary = "Lấy page theo pageKey",
      description = "Lấy thông tin page theo pageKey (slug). Trả về object rỗng nếu không tìm thấy.")
  @ApiResponse(responseCode = "200", description = "Lấy page thành công")
  public ResponseEntity<ApiResponseDto<PageAdminDto>> getPageByKey(
      @Parameter(description = "PageKey (slug) của page") @PathVariable String pageKey) {

    PageAdminDto result = pageService.getByPageKey(pageKey);
    return ResponseUtils.success(result, "Lấy page thành công");
  }

  @PostMapping
  @Operation(summary = "Tạo mới page", description = "Tạo page mới với thông tin được cung cấp")
  @ApiResponse(responseCode = "201", description = "Tạo page thành công")
  @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc slug đã tồn tại")
  public ResponseEntity<ApiResponseDto<PageAdminDto>> createPage(
      @Valid @RequestBody PageCreateDto dto) {

    PageAdminDto result = pageService.create(dto);
    return ResponseUtils.success(result, "Tạo page thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật page", description = "Cập nhật thông tin page theo ID")
  @ApiResponse(responseCode = "200", description = "Cập nhật page thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy page")
  @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc slug đã tồn tại")
  public ResponseEntity<ApiResponseDto<PageAdminDto>> updatePage(
      @Parameter(description = "ID của page") @PathVariable Long id,
      @Valid @RequestBody PageUpdateDto dto) {

    PageAdminDto result = pageService.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật page thành công");
  }
}
