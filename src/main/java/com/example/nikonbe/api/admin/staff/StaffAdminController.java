package com.example.nikonbe.api.admin.staff;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.staff.dto.request.StaffCreateDTO;
import com.example.nikonbe.modules.staff.dto.request.StaffUpdateDTO;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import com.example.nikonbe.modules.staff.service.interF.StaffService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/staffs")
@RequiredArgsConstructor
@Tag(name = "Admin - Staff Management", description = "Các API quản lý staff cho admin")
public class StaffAdminController {

  private final StaffService staffService;

  @PostMapping
  @Operation(
      summary = "Tạo mới staff",
      description = "Tạo mới staff với validate trùng username, email, số điện thoại")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Tạo staff thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc trùng lặp"),
    @ApiResponse(responseCode = "500", description = "Lỗi server")
  })
  public ResponseEntity<ApiResponseDto<StaffResponseDTO>> createStaff(
      @Valid @RequestBody StaffCreateDTO createDTO) {

    log.info("Creating staff with username: {}", createDTO.getUsername());
    StaffResponseDTO result = staffService.create(createDTO);

    return ResponseUtils.success(result, "Tạo staff thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Cập nhật staff",
      description = "Cập nhật thông tin staff với validate trùng email, số điện thoại")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật staff thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc trùng lặp"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff"),
    @ApiResponse(responseCode = "500", description = "Lỗi server")
  })
  public ResponseEntity<ApiResponseDto<StaffResponseDTO>> updateStaff(
      @Parameter(description = "ID của staff cần cập nhật", required = true) @PathVariable
          Integer id,
      @Valid @RequestBody StaffUpdateDTO updateDTO) {

    log.info("Updating staff with ID: {}", id);
    StaffResponseDTO result = staffService.update(id, updateDTO);

    return ResponseUtils.success(result, "Cập nhật staff thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy thông tin staff theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thông tin staff thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff")
  })
  public ResponseEntity<ApiResponseDto<StaffResponseDTO>> getStaffById(
      @Parameter(description = "ID của staff", required = true) @PathVariable Integer id) {

    StaffResponseDTO result = staffService.getById(id);
    return ResponseUtils.success(result, "Lấy thông tin staff thành công");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách staff",
      description = "Lấy danh sách staff với các tùy chọn lọc và phân trang")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy danh sách staff thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<StaffResponseDTO>>> getAllStaffs(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo họ tên") @RequestParam(required = false)
          String fullName,
      @Parameter(description = "Tìm kiếm theo số điện thoại") @RequestParam(required = false)
          String phoneNumber,
      @Parameter(description = "Tìm kiếm theo vai trò") @RequestParam(required = false)
          UserRole role,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<StaffResponseDTO> result = staffService.getAll(fullName, phoneNumber, role, status);
      return ResponseUtils.success(result, "Lấy danh sách staff thành công");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<StaffResponseDTO> result =
        staffService.getAllPaginated(fullName, phoneNumber, role, status, pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách staff thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa mềm staff", description = "Đánh dấu staff với trạng thái DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa staff thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff")
  })
  public ResponseEntity<ApiResponseDto<Void>> deleteStaff(
      @Parameter(description = "ID của staff cần xóa", required = true) @PathVariable Integer id) {

    log.info("Soft deleting staff with ID: {}", id);
    staffService.delete(id);

    return ResponseUtils.success(null, "Xóa staff thành công");
  }

  @DeleteMapping("/{id}/hard")
  @Operation(
      summary = "Xóa cứng staff",
      description = "Xóa staff hoàn toàn khỏi database (sử dụng cẩn thận)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa staff hoàn toàn thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff")
  })
  public ResponseEntity<ApiResponseDto<Void>> hardDeleteStaff(
      @Parameter(description = "ID của staff cần xóa hoàn toàn", required = true) @PathVariable
          Integer id) {

    log.info("Hard deleting staff with ID: {}", id);
    staffService.hardDelete(id);

    return ResponseUtils.success(null, "Xóa staff hoàn toàn thành công");
  }
}
