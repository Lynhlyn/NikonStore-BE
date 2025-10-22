package com.example.nikonbe.api.admin.banner;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.banner.dto.request.BannerCreateDTO;
import com.example.nikonbe.modules.banner.dto.request.BannerUpdateDTO;
import com.example.nikonbe.modules.banner.dto.response.BannerResponseDTO;
import com.example.nikonbe.modules.banner.service.interF.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/banners")
@RequiredArgsConstructor
@Tag(name = "Admin - Banner Management", description = "Các API quản lý banner cho admin")
public class BannerAdminController {

  private final BannerService bannerService;

  @PostMapping
  @Operation(summary = "Tạo mới banner", description = "Tạo banner mới với thông tin được cung cấp")
  @ApiResponse(responseCode = "201", description = "Tạo banner thành công")
  @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
  public ResponseEntity<ApiResponseDto<BannerResponseDTO>> createBanner(
      @Valid @RequestBody BannerCreateDTO dto) {
    
    BannerResponseDTO result = bannerService.create(dto);
    return ResponseUtils.success(result, "Tạo banner thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật banner", description = "Cập nhật thông tin banner theo ID")
  @ApiResponse(responseCode = "200", description = "Cập nhật banner thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy banner")
  public ResponseEntity<ApiResponseDto<BannerResponseDTO>> updateBanner(
      @Parameter(description = "ID của banner") 
      @PathVariable Long id, 
      @Valid @RequestBody BannerUpdateDTO dto) {
    
    BannerResponseDTO result = bannerService.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật banner thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy banner theo ID", description = "Lấy thông tin chi tiết banner theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy banner thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy banner")
  public ResponseEntity<ApiResponseDto<BannerResponseDTO>> getBannerById(
      @Parameter(description = "ID của banner") 
      @PathVariable Long id) {
    
    BannerResponseDTO result = bannerService.getById(id);
    return ResponseUtils.success(result, "Lấy banner thành công");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách banner",
      description = "Lấy danh sách banner với phân trang và bộ lọc. " +
                   "Nếu isAll=true, trả về tất cả banner không phân trang. " +
                   "Nếu isAll=false, phân trang theo các tham số page, size, sort, direction.")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách banner thành công")
  public ResponseEntity<ApiResponseDto<List<BannerResponseDTO>>> getAllBanners(
      @Parameter(description = "Lấy tất cả banner không phân trang") 
      @RequestParam(defaultValue = "false") boolean isAll,
      
      @Parameter(description = "Trạng thái banner") 
      @RequestParam(required = false) Status status,
      
      @Parameter(description = "Vị trí banner") 
      @RequestParam(required = false) String position,
      
      @Parameter(description = "Số trang") 
      @RequestParam(defaultValue = "0") int page,
      
      @Parameter(description = "Kích thước trang") 
      @RequestParam(defaultValue = "10") int size,
      
      @Parameter(description = "Trường sắp xếp") 
      @RequestParam(defaultValue = "displayOrder") String sort,
      
      @Parameter(description = "Hướng sắp xếp") 
      @RequestParam(defaultValue = "asc") String direction) {

    if (isAll) {
      List<BannerResponseDTO> result = bannerService.getAll(status, position);
      return ResponseUtils.success(result, "Lấy tất cả banner thành công");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<BannerResponseDTO> result = bannerService.getAllPaginated(status, position, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách banner thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Xóa banner",
      description = "Xóa banner bằng cách chuyển trạng thái sang DELETED")
  @ApiResponse(responseCode = "200", description = "Xóa banner thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy banner")
  public ResponseEntity<ApiResponseDto<Void>> deleteBanner(
      @Parameter(description = "ID của banner") 
      @PathVariable Long id) {
    
    bannerService.delete(id);
    return ResponseUtils.success(null, "Xóa banner thành công");
  }
}
