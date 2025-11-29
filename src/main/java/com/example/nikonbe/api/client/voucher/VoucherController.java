package com.example.nikonbe.api.client.voucher;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.voucher.dto.response.VoucherDiscountResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import com.example.nikonbe.modules.voucher.service.interF.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/vouchers")
@RequiredArgsConstructor
@Tag(name = "Client - Voucher", description = "Các API voucher dành cho client")
public class VoucherController {

  private final VoucherService voucherService;

  @GetMapping("/public/active")
  @Operation(
      summary = "Lấy danh sách voucher công khai đang hoạt động",
      description = "Lấy danh sách voucher công khai đang hoạt động")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<VoucherResponseDTO>>> getPublicActiveVouchers() {
    List<VoucherResponseDTO> result = voucherService.getPublicActiveVouchers();
    return ResponseUtils.success(result, "Lấy danh sách voucher công khai thành công");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách voucher khả dụng cho khách hàng",
      description =
          "Lấy danh sách voucher khả dụng bao gồm public và private được cấp quyền. "
              + "Voucher được sắp xếp theo thời gian kết thúc gần nhất (ưu tiên voucher sắp hết hạn). "
              + "Hỗ trợ lọc, sắp xếp và phân trang.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "CustomerId không hợp lệ",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<VoucherResponseDTO>>> getAvailableVouchers(
      @Parameter(description = "ID khách hàng (bắt buộc)", required = true) @RequestParam
          Integer customerId,
      @Parameter(description = "Mã voucher") @RequestParam(required = false) String code,
      @Parameter(description = "Loại giảm giá") @RequestParam(required = false) String discountType,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "endDate")
          String sortBy,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc") String sortDir,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size) {

    if (customerId == null || customerId <= 0) {
      return ResponseUtils.error("CustomerId không hợp lệ", HttpStatus.BAD_REQUEST);
    }

    try {
      ApiResponseDto<List<VoucherResponseDTO>> response =
          voucherService.getAllVouchers(
              code, null, discountType, null, false, sortBy, sortDir, page, size, customerId);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      return ResponseUtils.error(
          "Có lỗi xảy ra khi lấy danh sách voucher", HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
  public ResponseEntity<ApiResponseDto<VoucherResponseDTO>> getVoucherByCode(
      @Parameter(description = "Mã voucher") @PathVariable String code,
      @Parameter(description = "ID khách hàng (optional)") @RequestParam(required = false)
          Integer customerId) {
    VoucherResponseDTO result = voucherService.getByCode(code, customerId);
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
