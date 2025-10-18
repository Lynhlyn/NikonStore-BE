package com.example.nikonbe.api.admin.customer;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/v1/customers")
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
      @Valid @RequestBody CustomerCreateDTO dto) {
    CustomerResponseDTO result = customerService.create(dto);
    return ResponseUtils.success(result, "Customer created successfully", HttpStatus.CREATED);
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
    CustomerResponseDTO result = customerService.update(id, dto);
    return ResponseUtils.success(result, "Customer updated successfully");
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

  @GetMapping
  @Operation(
      summary = "Get customers list with advanced filters",
      description = "Retrieve paginated list of customers with advanced filtering options")
  @ApiResponse(
      responseCode = "200",
      description = "Customers retrieved successfully",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<Page<CustomerResponseDTO>>> getAllWithFilters(
      @Parameter(description = "Advanced filter criteria") @ModelAttribute
          CustomerFilterDTO filterDTO,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerResponseDTO> result =
        customerService.getCustomersWithAdvancedFilters(filterDTO, pageable);
    return ResponseUtils.successWithPage(result, "Customers retrieved successfully");
  }

  @GetMapping("/simple")
  @Operation(
      summary = "Get customers list (simple)",
      description = "Retrieve paginated list of customers with basic filtering")
  @ApiResponse(
      responseCode = "200",
      description = "Customers retrieved successfully",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<Page<CustomerResponseDTO>>> getAllSimple(
      @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
      @Parameter(description = "Filter by status") @RequestParam(required = false) Status status,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CustomerResponseDTO> result = customerService.getAll(keyword, status, pageable);
    return ResponseUtils.successWithPage(result, "Customers retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete customer", description = "Soft delete a customer account (Admin)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer deleted successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "Customer ID") @PathVariable Integer id) {
    customerService.delete(id);
    return ResponseUtils.success(null, "Customer deleted successfully");
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Toggle customer status", description = "Change customer status (Admin)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer status updated successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> toggleStatus(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @Parameter(description = "New status") @RequestParam Status status) {
    CustomerResponseDTO result = customerService.toggleStatus(id, status);
    return ResponseUtils.success(result, "Customer status updated successfully");
  }

  @PutMapping("/{id}/block")
  @Operation(summary = "Block customer", description = "Block a customer account (Admin)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer blocked successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> blockCustomer(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @Parameter(description = "Block reason") @RequestParam String reason) {
    CustomerResponseDTO result = customerService.blockCustomer(id, reason);
    return ResponseUtils.success(result, "Customer blocked successfully");
  }

  @PutMapping("/{id}/unblock")
  @Operation(summary = "Unblock customer", description = "Unblock a customer account (Admin)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer unblocked successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> unblockCustomer(
      @Parameter(description = "Customer ID") @PathVariable Integer id) {
    CustomerResponseDTO result = customerService.unblockCustomer(id);
    return ResponseUtils.success(result, "Customer unblocked successfully");
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
