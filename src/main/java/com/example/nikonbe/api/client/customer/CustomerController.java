package com.example.nikonbe.api.client.customer;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Client - Customer API", description = "Customer management APIs for clients")
public class CustomerController {

  private final CustomerService customerService;

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
      @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc")
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
      @Parameter(description = "Customer ID") @PathVariable Integer id) {
    customerService.delete(id);
    return ResponseUtils.success(null, "Customer deleted successfully");
  }
}
