package com.example.nikonbe.api.admin.customervoucher;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customervoucher.dto.request.CustomerVoucherAssignDTO;
import com.example.nikonbe.modules.customervoucher.dto.response.CustomerVoucherResponseDTO;
import com.example.nikonbe.modules.customervoucher.service.interF.CustomerVoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.admin.version}/customer-vouchers")
@RequiredArgsConstructor
@Tag(
    name = "Admin - Customer Voucher Management",
    description = "Các API quản lý voucher của khách hàng dành cho admin")
public class CustomerVoucherAdminController {

  private final CustomerVoucherService customerVoucherService;

  @PostMapping("/assign")
  @Operation(summary = "Gán voucher cho khách hàng")
  @ApiResponse(
      responseCode = "201",
      description = "Gán thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> assignVouchersToCustomer(
      @Valid @RequestBody CustomerVoucherAssignDTO dto) {
    List<CustomerVoucherResponseDTO> result = customerVoucherService.assignVouchersToCustomer(dto);
    return ResponseUtils.success(
        result, "Gán voucher cho khách hàng thành công", HttpStatus.CREATED);
  }

  @PostMapping("/assign-bulk")
  @Operation(summary = "Gán một voucher cho nhiều khách hàng")
  @ApiResponse(
      responseCode = "200",
      description = "Gán thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<Void>> assignVoucherToCustomers(
      @RequestParam @Parameter(description = "ID voucher cần gán") Long voucherId,
      @RequestBody List<Integer> customerIds) {
    customerVoucherService.assignVoucherToCustomers(voucherId, customerIds);
    return ResponseUtils.success(null, "Gán voucher cho nhiều khách hàng thành công");
  }

  @GetMapping("/customer/{customerId}")
  @Operation(summary = "Lấy danh sách voucher của khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> getVouchersByCustomer(
      @Parameter(description = "ID khách hàng") @PathVariable Integer customerId,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "voucher.id")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerVoucherResponseDTO> result =
        customerVoucherService.getVouchersByCustomerIdPaginated(customerId, pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách voucher của khách hàng thành công");
  }

  @GetMapping("/customer/{customerId}/unused")
  @Operation(summary = "Lấy danh sách voucher chưa sử dụng của khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>>
      getUnusedVouchersByCustomer(
          @Parameter(description = "ID khách hàng") @PathVariable Integer customerId) {
    List<CustomerVoucherResponseDTO> result =
        customerVoucherService.getUnusedVouchersByCustomerId(customerId);
    return ResponseUtils.success(
        result, "Lấy danh sách voucher chưa sử dụng của khách hàng thành công");
  }

  @GetMapping("/customer/{customerId}/used")
  @Operation(summary = "Lấy danh sách voucher đã sử dụng của khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> getUsedVouchersByCustomer(
      @Parameter(description = "ID khách hàng") @PathVariable Integer customerId) {
    List<CustomerVoucherResponseDTO> result =
        customerVoucherService.getUsedVouchersByCustomerId(customerId);
    return ResponseUtils.success(
        result, "Lấy danh sách voucher đã sử dụng của khách hàng thành công");
  }

  @PostMapping("/customer/{customerId}/voucher/{voucherId}/use")
  @Operation(summary = "Đánh dấu voucher đã được khách hàng sử dụng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Đánh dấu thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy voucher của khách hàng"),
    @ApiResponse(responseCode = "400", description = "Voucher đã được sử dụng")
  })
  public ResponseEntity<ApiResponseDto<CustomerVoucherResponseDTO>> useVoucher(
      @Parameter(description = "ID khách hàng") @PathVariable Integer customerId,
      @Parameter(description = "ID voucher") @PathVariable Long voucherId) {
    CustomerVoucherResponseDTO result = customerVoucherService.useVoucher(customerId, voucherId);
    return ResponseUtils.success(result, "Đánh dấu voucher đã sử dụng thành công");
  }

  @DeleteMapping("/customer/{customerId}/voucher/{voucherId}")
  @Operation(summary = "Xóa liên kết voucher với khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy voucher của khách hàng")
  })
  public ResponseEntity<ApiResponseDto<Void>> removeVoucherFromCustomer(
      @Parameter(description = "ID khách hàng") @PathVariable Integer customerId,
      @Parameter(description = "ID voucher") @PathVariable Long voucherId) {
    customerVoucherService.removeVoucherFromCustomer(customerId, voucherId);
    return ResponseUtils.success(null, "Xóa liên kết voucher với khách hàng thành công");
  }

  @GetMapping("/customer/{customerId}/statistics")
  @Operation(summary = "Thống kê voucher của khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thống kê thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<Map<String, Long>>> getCustomerVoucherStatistics(
      @Parameter(description = "ID khách hàng") @PathVariable Integer customerId) {

    long totalVouchers = customerVoucherService.countVouchersByCustomerId(customerId);
    long usedVouchers = customerVoucherService.countUsedVouchersByCustomerId(customerId);
    long unusedVouchers = totalVouchers - usedVouchers;

    Map<String, Long> statistics =
        Map.of(
            "totalVouchers", totalVouchers,
            "usedVouchers", usedVouchers,
            "unusedVouchers", unusedVouchers);

    return ResponseUtils.success(statistics, "Lấy thống kê voucher của khách hàng thành công");
  }

  @GetMapping("/check")
  @Operation(summary = "Kiểm tra khách hàng có voucher")
  @ApiResponse(responseCode = "200", description = "Kiểm tra thành công")
  public ResponseEntity<ApiResponseDto<Boolean>> checkCustomerHasVoucher(
      @Parameter(description = "ID khách hàng") @RequestParam Integer customerId,
      @Parameter(description = "ID voucher") @RequestParam Long voucherId) {
    boolean hasVoucher = customerVoucherService.customerHasVoucher(customerId, voucherId);
    return ResponseUtils.success(hasVoucher, "Kiểm tra khách hàng có voucher thành công");
  }

  @GetMapping("/voucher/{voucherId}/customers")
  @Operation(summary = "Lấy danh sách khách hàng đang được gán cho voucher")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy voucher")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> getCustomersByVoucherId(
      @Parameter(description = "ID voucher") @PathVariable Long voucherId,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "customer.id")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerVoucherResponseDTO> result =
        customerVoucherService.getCustomersByVoucherIdPaginated(voucherId, pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách khách hàng của voucher thành công");
  }
}
