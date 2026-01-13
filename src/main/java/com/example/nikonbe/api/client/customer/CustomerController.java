package com.example.nikonbe.api.client.customer;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.dto.request.ChangePasswordDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerClientUpdateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.request.CustomerUpdateDTO;
import com.example.nikonbe.modules.customer.dto.request.DeleteCustomerDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.service.interF.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.version}/customers")
@RequiredArgsConstructor
@Tag(name = "Client - Customer API", description = "Customer management APIs for clients")
public class CustomerController {

  private final CustomerService customerService;
  private final ObjectMapper objectMapper;

  @PostMapping
  @Operation(summary = "Create a new customer", description = "Register a new customer account")
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
      description = "Update customer profile information")
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

  @GetMapping("/current")
  @Operation(
      summary = "Get current customer",
      description = "Retrieve current logged-in customer information from token")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer retrieved successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> getCurrentUser(
      Authentication authentication) {
    CustomerResponseDTO result = customerService.getCurrentUser(authentication);
    return ResponseUtils.success(result, "Customer retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get customer by ID", description = "Retrieve customer information by ID")
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
      summary = "Get customers list",
      description = "Retrieve paginated list of customers with optional filtering")
  @ApiResponse(
      responseCode = "200",
      description = "Customers retrieved successfully",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CustomerResponseDTO>>> getAll(
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
  @Operation(summary = "Delete customer", description = "Soft delete a customer account")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Customer deleted successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @Valid @RequestBody DeleteCustomerDTO dto) {
    customerService.delete(id, dto.getReason());
    return ResponseUtils.success(null, "Customer deleted successfully");
  }

  @PutMapping(path = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Update customer profile",
      description = "Update customer profile with optional image")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Profile updated successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Invalid data"),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> updateProfile(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @RequestPart("customer") String customerJson,
      @RequestPart(value = "image", required = false) MultipartFile image)
      throws IOException {
    try {
      CustomerClientUpdateDTO dto =
          objectMapper.readValue(customerJson, CustomerClientUpdateDTO.class);
      CustomerResponseDTO result = customerService.updateClientInfo(id, dto, image);
      return ResponseUtils.success(result, "Profile updated successfully");
    } catch (JsonProcessingException e) {
      ApiResponseDto<CustomerResponseDTO> errorResponse =
          ApiResponseDto.<CustomerResponseDTO>builder()
              .status(HttpStatus.BAD_REQUEST.value())
              .message("Invalid JSON data: " + e.getMessage())
              .build();
      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  @PostMapping("/{id}/change-password")
  @Operation(summary = "Change password", description = "Change customer password")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Password changed successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid password or current password incorrect"),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<Void>> changePassword(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @Valid @RequestBody ChangePasswordDTO dto) {
    customerService.changePassword(id, dto);
    return ResponseUtils.success(null, "Đổi mật khẩu thành công");
  }

  @PutMapping("/{id}/deactivate")
  @Operation(summary = "Deactivate account", description = "Deactivate customer account")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Account deactivated successfully",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  public ResponseEntity<ApiResponseDto<Void>> deactivateAccount(
      @Parameter(description = "Customer ID") @PathVariable Integer id,
      @RequestBody(required = false) java.util.Map<String, String> request) {
    String reason =
        (request != null && request.get("reason") != null)
            ? request.get("reason")
            : "Customer requested deactivation";
    customerService.delete(id, reason);
    return ResponseUtils.success(null, "Account deactivated successfully");
  }
}
