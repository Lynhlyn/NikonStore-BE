package com.example.nikonbe.api.admin.customer;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.dto.request.BlockCustomerDTO;
import com.example.nikonbe.modules.customer.dto.request.CreateCustomerDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerFilterDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.service.interF.CustomerService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/customers")
@RequiredArgsConstructor
@Tag(
    name = "Admin - Customer Management",
    description = "Customer management APIs for administrators")
public class CustomerAdminController {

  private final CustomerService customerService;

  @PostMapping
  @Operation(
      summary = "Create a new customer",
      description = "Create a new customer account (Admin)")
  @ApiResponse(
      responseCode = "201",
      description = "Customer created successfully",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> create(
      @Valid @RequestBody CreateCustomerDTO dto) {
    CustomerResponseDTO result = customerService.adminCreated(dto);
    return ResponseUtils.success(result, "Tạo tài khoản thành công.", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update customer information",
      description = "Update customer profile information (Admin)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer updated successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> update(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @Valid @RequestBody CustomerUpdateDTO dto) {
    CustomerResponseDTO result = customerService.adminUpdate(id, dto);
    return ResponseUtils.success(result, "Cập nhật tài khoản thành công.");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get customer by ID",
      description = "Retrieve customer information by ID (Admin)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer retrieved successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> getById(
      @Parameter(description = "Customer ID") @PathVariable Integer id) {
    CustomerResponseDTO result = customerService.getById(id);
    return ResponseUtils.success(result, "Customer retrieved successfully");
  }

  @PostMapping("/filter")
  @Operation(
      summary = "Lọc danh sách khách hàng nâng cao",
      description =
          "Hỗ trợ lọc theo nhiều tiêu chí: keyword, status, email, phone, fullName, gender, provider, isGuest, ngày tạo, ngày cập nhật")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<CustomerResponseDTO>>> getCustomersWithAdvancedFilters(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Các tiêu chí lọc khách hàng",
              required = true)
          @RequestBody
          CustomerFilterDTO filterDTO,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerResponseDTO> result =
        customerService.getCustomersWithAdvancedFilters(filterDTO, pageable);
    return ResponseUtils.successWithPage(result, "Lọc danh sách khách hàng thành công");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách tài khoản khách hàng",
      description = "Hỗ trợ tìm kiếm và lọc theo trạng thái")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<CustomerResponseDTO>>> getAll(
      @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String keyword,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerResponseDTO> result = customerService.getAll(keyword, status, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách tài khoản khách hàng thành công");
  }

  @GetMapping("/simple")
  @Operation(
      summary = "Get customers list (simple)",
      description = "Retrieve paginated list of customers with basic filtering")
  @ApiResponse(
      responseCode = "200",
      description = "Customers retrieved successfully",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CustomerResponseDTO>>> getAllSimple(
      @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
      @Parameter(description = "Filter by status") @RequestParam(required = false) Status status,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerResponseDTO> result = customerService.getAll(keyword, status, pageable);
    return ResponseUtils.successWithPage(result, "Customers retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xoá tài khoản", description = "Đánh dấu tài khoản là đã xoá")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xoá thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID tài khoản") @PathVariable Integer id,
      @RequestBody String reason) {
    customerService.delete(id);
    return ResponseUtils.success(null, "Xoá tài khoản thành công.");
  }

  @PatchMapping("/{id}/toggle-status")
  @Operation(summary = "Thay đổi trạng thái tài khoản khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật trạng thái thành công.",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản.")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> toggleStatus(
      @Parameter(description = "ID tài khoản") @PathVariable Integer id,
      @RequestBody ToggleStatusRequest statusRequest) {
    CustomerResponseDTO result = customerService.toggleStatus(id, statusRequest.getStatus());
    return ResponseUtils.success(result, "Cập nhật trạng thái tài khoản thành công.");
  }

  @PostMapping("/{id}/block")
  @Operation(summary = "Khoá tài khoản khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Khoá tài khoản thành công.",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản."),
    @ApiResponse(
        responseCode = "400",
        description = "Tài khoản đã bị khoá hoặc dữ liệu không hợp lệ.")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> blockCustomer(
      @Parameter(description = "ID tài khoản") @PathVariable Integer id,
      @Valid @RequestBody BlockCustomerDTO blockRequest) {
    CustomerResponseDTO result = customerService.blockCustomer(id, blockRequest.getReason());
    return ResponseUtils.success(result, "Khoá tài khoản thành công.");
  }

  @PostMapping("/{id}/unblock")
  @Operation(summary = "Mở khoá tài khoản khách hàng")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Mở khoá tài khoản thành công.",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản."),
    @ApiResponse(responseCode = "400", description = "Tài khoản hiện không ở trạng thái bị khoá.")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> unblockCustomer(
      @Parameter(description = "ID tài khoản") @PathVariable Integer id) {
    CustomerResponseDTO result = customerService.unblockCustomer(id);
    return ResponseUtils.success(result, "Mở khoá tài khoản thành công.");
  }

  // Inner class for toggle status request
  public static class ToggleStatusRequest {
    private Status status;

    public Status getStatus() {
      return status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }
  }

  @GetMapping("/check-username")
  @Operation(
      summary = "Check username availability",
      description = "Check if username is available")
  @ApiResponse(
      responseCode = "200",
      description = "Username availability checked",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<Boolean>> checkUsername(
      @Parameter(description = "Username to check") @RequestParam String username) {
    boolean exists = customerService.existsByUsername(username);
    return ResponseUtils.success(!exists, exists ? "Username is taken" : "Username is available");
  }

  @GetMapping("/check-email")
  @Operation(summary = "Check email availability", description = "Check if email is available")
  @ApiResponse(
      responseCode = "200",
      description = "Email availability checked",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<Boolean>> checkEmail(
      @Parameter(description = "Email to check") @RequestParam String email) {
    boolean exists = customerService.existsByEmail(email);
    return ResponseUtils.success(!exists, exists ? "Email is taken" : "Email is available");
  }

  @GetMapping("/check-phone")
  @Operation(
      summary = "Check phone number availability",
      description = "Check if phone number is available")
  @ApiResponse(
      responseCode = "200",
      description = "Phone number availability checked",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<Boolean>> checkPhone(
      @Parameter(description = "Phone number to check") @RequestParam String phoneNumber) {
    boolean exists = customerService.existsByPhoneNumber(phoneNumber);
    return ResponseUtils.success(
        !exists, exists ? "Phone number is taken" : "Phone number is available");
  }
}
