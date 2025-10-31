package com.example.nikonbe.api.client.customervoucher;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customervoucher.dto.response.CustomerVoucherResponseDTO;
import com.example.nikonbe.modules.customervoucher.service.interF.CustomerVoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}/customer-vouchers")
@RequiredArgsConstructor
@Tag(
    name = "Client - Customer Voucher API",
    description = "Các API voucher của khách hàng dành cho người dùng")
public class CustomerVoucherController {

  private final CustomerVoucherService customerVoucherService;

  @GetMapping("/my-vouchers")
  @Operation(
      summary = "Lấy danh sách voucher của tôi",
      description = "Lấy voucher của khách hàng đang đăng nhập")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> getMyVouchers(
      @Parameter(description = "ID khách hàng") @RequestParam Integer customerId,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "voucher.id")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerVoucherResponseDTO> result =
        customerVoucherService.getVouchersByCustomerIdPaginated(customerId, pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách voucher của tôi thành công");
  }

  @GetMapping("/my-vouchers/unused")
  @Operation(summary = "Lấy danh sách voucher chưa sử dụng của tôi")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> getMyUnusedVouchers(
      @Parameter(description = "ID khách hàng", hidden = true) @RequestParam Integer customerId) {
    List<CustomerVoucherResponseDTO> result =
        customerVoucherService.getUnusedVouchersByCustomerId(customerId);
    return ResponseUtils.success(result, "Lấy danh sách voucher chưa sử dụng của tôi thành công");
  }

  @GetMapping("/my-vouchers/used")
  @Operation(summary = "Lấy danh sách voucher đã sử dụng của tôi")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
  })
  public ResponseEntity<ApiResponseDto<List<CustomerVoucherResponseDTO>>> getMyUsedVouchers(
      @Parameter(description = "ID khách hàng", hidden = true) @RequestParam Integer customerId) {
    List<CustomerVoucherResponseDTO> result =
        customerVoucherService.getUsedVouchersByCustomerId(customerId);
    return ResponseUtils.success(result, "Lấy danh sách voucher đã sử dụng của tôi thành công");
  }

  @GetMapping("/check")
  @Operation(summary = "Kiểm tra tôi có voucher")
  @ApiResponse(responseCode = "200", description = "Kiểm tra thành công")
  public ResponseEntity<ApiResponseDto<Boolean>> checkHasVoucher(
      @Parameter(description = "ID khách hàng", hidden = true) @RequestParam Integer customerId,
      @Parameter(description = "ID voucher") @RequestParam Long voucherId) {
    boolean hasVoucher = customerVoucherService.customerHasVoucher(customerId, voucherId);
    return ResponseUtils.success(hasVoucher, "Kiểm tra tôi có voucher thành công");
  }

  @PostMapping("/use")
  @Operation(summary = "Sử dụng voucher")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Sử dụng thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy voucher của khách hàng"),
    @ApiResponse(responseCode = "400", description = "Voucher đã được sử dụng")
  })
  public ResponseEntity<ApiResponseDto<CustomerVoucherResponseDTO>> useVoucher(
      @Parameter(description = "ID khách hàng") @RequestParam Integer customerId,
      @Parameter(description = "ID voucher") @RequestParam Long voucherId) {
    CustomerVoucherResponseDTO result = customerVoucherService.useVoucher(customerId, voucherId);
    return ResponseUtils.success(result, "Sử dụng voucher thành công");
  }
}
