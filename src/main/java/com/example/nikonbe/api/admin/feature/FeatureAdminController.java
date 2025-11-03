package com.example.nikonbe.api.admin.feature;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.feature.dto.request.FeatureCreateDTO;
import com.example.nikonbe.modules.feature.dto.request.FeatureUpdateDTO;
import com.example.nikonbe.modules.feature.dto.response.FeatureResponseDTO;
import com.example.nikonbe.modules.feature.service.interF.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${api.admin.version}/features")
@RequiredArgsConstructor
@Tag(name = "Admin - Feature Management", description = "API quản lý tính năng")
public class FeatureAdminController {

  private final FeatureService featureService;

  @PostMapping
  @Operation(summary = "Tạo mới tính năng")
  @ApiResponse(responseCode = "201", description = "Tạo thành công")
  public ResponseEntity<ApiResponseDto<FeatureResponseDTO>> create(
      @Valid @RequestBody FeatureCreateDTO dto) {
    FeatureResponseDTO result = featureService.create(dto);
    return ResponseUtils.success(result, "Feature created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật tính năng")
  @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
  public ResponseEntity<ApiResponseDto<FeatureResponseDTO>> update(
      @Parameter(description = "ID tính năng") @PathVariable Integer id,
      @Valid @RequestBody FeatureUpdateDTO dto) {
    FeatureResponseDTO result = featureService.update(id, dto);
    return ResponseUtils.success(result, "Feature updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy tính năng theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<FeatureResponseDTO>> getById(
      @Parameter(description = "ID tính năng") @PathVariable Integer id) {
    FeatureResponseDTO result = featureService.getById(id);
    return ResponseUtils.success(result, "Feature retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách tính năng",
      description = "Hỗ trợ phân trang và tìm kiếm theo tên và nhóm tính năng")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<FeatureResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String name,
      @Parameter(description = "Lọc theo nhóm tính năng") @RequestParam(required = false)
          String featureGroup,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<FeatureResponseDTO> result = featureService.getAll(name, featureGroup);
      return ResponseUtils.success(result, "Features retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<FeatureResponseDTO> result = featureService.getAllPaginated(name, featureGroup, pageable);
    return ResponseUtils.successWithPage(result, "Features retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa tính năng")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID tính năng") @PathVariable Integer id) {
    featureService.delete(id);
    return ResponseUtils.success(null, "Feature deleted successfully");
  }
}
