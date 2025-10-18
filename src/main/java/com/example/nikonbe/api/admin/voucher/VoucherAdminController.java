package com.example.nikonbe.api.admin.voucher;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.voucher.dto.request.VoucherCreateDTO;
import com.example.nikonbe.modules.voucher.dto.request.VoucherUpdateDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherDiscountResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import com.example.nikonbe.modules.voucher.service.interF.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/vouchers")
@RequiredArgsConstructor
@Tag(name = "Admin - Voucher Management", description = "Các API quản lý voucher dành cho admin")
public class VoucherAdminController {

  private final VoucherService voucherService;

  @PostMapping
  @Operation(summary = "Tạo voucher mới", description = "Tạo mới một voucher")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<VoucherResponseDTO>> create(
      @Valid @RequestBody VoucherCreateDTO dto) {
    VoucherResponseDTO result = voucherService.create(dto);
    return ResponseUtils.success(result, "Tạo voucher thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật voucher", description = "Cập nhật thông tin voucher theo ID")
  @ApiResponse(
      responseCode = "200",
      description = "Cập nhật thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<VoucherResponseDTO>> update(
      @Parameter(description = "ID voucher") @PathVariable Long id,
      @Valid @RequestBody VoucherUpdateDTO dto) {
    VoucherResponseDTO result = voucherService.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật voucher thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa voucher", description = "Đánh dấu voucher là DELETED (xóa mềm)")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID voucher") @PathVariable Long id) {
    voucherService.delete(id);
    return ResponseUtils.success(null, "Xóa voucher thành công");
  }

  @PatchMapping("/{id}/toggle-status")
  @Operation(
      summary = "Đổi trạng thái voucher",
      description = "Chuyển đổi trạng thái giữa ACTIVE và INACTIVE")
  @ApiResponse(
      responseCode = "200",
      description = "Đổi trạng thái thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<VoucherResponseDTO>> toggleStatus(
      @Parameter(description = "ID voucher") @PathVariable Long id) {
    VoucherResponseDTO result = voucherService.toggleStatus(id);
    return ResponseUtils.success(result, "Đổi trạng thái voucher thành công");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy thông tin voucher theo ID",
      description = "Lấy chi tiết voucher theo ID")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<VoucherResponseDTO>> getById(
      @Parameter(description = "ID voucher") @PathVariable Long id) {
    VoucherResponseDTO result = voucherService.getById(id);
    return ResponseUtils.success(result, "Lấy thông tin voucher thành công");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách voucher",
      description = "Lấy tất cả voucher với lọc và phân trang")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<VoucherResponseDTO>>> getAllVouchers(
      @Parameter(description = "Mã voucher") @RequestParam(required = false) String code,
      @Parameter(description = "Trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Loại giảm giá") @RequestParam(required = false) String discountType,
      @Parameter(description = "Công khai") @RequestParam(required = false) Boolean isPublic,
      Pageable pageable) {

    Page<VoucherResponseDTO> result =
        voucherService.getAllVouchers(code, status, discountType, isPublic, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách voucher thành công");
  }

  @GetMapping("/public/active")
  @Operation(
      summary = "Lấy danh sách voucher công khai đang hoạt động",
      description = "Lấy danh sách voucher công khai đang hoạt động")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<VoucherResponseDTO>>> getPublicActiveVouchers() {
    List<VoucherResponseDTO> result = voucherService.getPublicActiveVouchers();
    return ResponseUtils.success(result, "Lấy danh sách voucher công khai hoạt động thành công");
  }

  @GetMapping("/customer/{customerId}/available")
  @Operation(
      summary = "Lấy danh sách voucher khả dụng cho khách hàng",
      description = "Lấy danh sách voucher khả dụng cho khách hàng")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<VoucherResponseDTO>>> getAvailableVouchersForCustomer(
      @Parameter(description = "ID khách hàng") @PathVariable Integer customerId) {
    List<VoucherResponseDTO> result = voucherService.getAvailableVouchersForCustomer(customerId);
    return ResponseUtils.success(result, "Lấy danh sách voucher khả dụng thành công");
  }

  @GetMapping("/code/{code}")
  @Operation(
      summary = "Lấy thông tin voucher theo mã code",
      description = "Lấy thông tin voucher theo mã code")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<VoucherResponseDTO>> getByCode(
      @Parameter(description = "Mã code voucher") @PathVariable String code) {
    VoucherResponseDTO result = voucherService.getByCode(code);
    return ResponseUtils.success(result, "Lấy thông tin voucher thành công");
  }

  @PostMapping("/apply")
  @Operation(
      summary = "Áp dụng voucher cho đơn hàng",
      description = "Áp dụng voucher và trả về thông tin giảm giá")
  @ApiResponse(
      responseCode = "200",
      description = "Áp dụng thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<VoucherDiscountResponseDTO>> applyVoucher(
      @Parameter(description = "Mã voucher") @RequestParam String code,
      @Parameter(description = "ID khách hàng") @RequestParam Integer customerId,
      @Parameter(description = "Giá trị đơn hàng") @RequestParam BigDecimal orderValue) {
    VoucherDiscountResponseDTO result = voucherService.applyVoucher(code, customerId, orderValue);
    return ResponseUtils.success(result, "Áp dụng voucher thành công");
  }

  @GetMapping("/exists")
  @Operation(
      summary = "Kiểm tra voucher có tồn tại theo mã",
      description = "Kiểm tra voucher có tồn tại theo mã")
  @ApiResponse(responseCode = "200", description = "Kiểm tra thành công")
  public ResponseEntity<ApiResponseDto<Boolean>> checkVoucherExists(
      @Parameter(description = "Mã voucher") @RequestParam String code) {
    Boolean result = voucherService.existsByCode(code);
    return ResponseUtils.success(result, "Kiểm tra tồn tại voucher thành công");
  }
}
