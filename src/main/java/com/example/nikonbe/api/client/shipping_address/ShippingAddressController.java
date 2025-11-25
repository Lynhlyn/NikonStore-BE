package com.example.nikonbe.api.client.shipping_address;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.shipping_address.dto.request.CreateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.request.UpdateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.response.ShippingAddressResponseDto;
import com.example.nikonbe.modules.shipping_address.service.ShippingAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${api.version}/shipping-addresses")
@RequiredArgsConstructor
@Tag(name = "Client - Shipping Address Management", description = "API quản lý địa chỉ giao hàng")
public class ShippingAddressController {

  private final ShippingAddressService shippingAddressService;

  /** Thêm địa chỉ mới */
  @PostMapping
  @Operation(summary = "Tạo địa chỉ mới", description = "Tạo địa chỉ mới với các thông tin cơ bản")
  @ApiResponse(responseCode = "201", description = "Tạo địa chỉ thành công")
  public ResponseEntity<ApiResponseDto<ShippingAddressResponseDto>> createAddress(
      @Valid @RequestBody CreateShippingAddressDTO dto) {
    ShippingAddressResponseDto result = shippingAddressService.create(dto);
    return ResponseUtils.success(result, "Thêm địa chỉ mới thành công", HttpStatus.CREATED);
  }

  /** Cập nhật thông tin địa chỉ. */
  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật địa chỉ", description = "Cập nhật thông tin cơ bản của địa chỉ")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Cập nhật địa chỉ thành công"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
  })
  public ResponseEntity<ApiResponseDto<ShippingAddressResponseDto>> updateAddress(
      @Parameter(description = "ID địa chỉ") @PathVariable Integer id,
      @Valid @RequestBody UpdateShippingAddressDTO dto) {

    ShippingAddressResponseDto result = shippingAddressService.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật địa chỉ thành công");
  }

  /** Lấy danh sách địa chỉ đơn giản */
  @GetMapping("/customer/{customerId}")
  @Operation(
      summary = "Lấy danh sách địa chỉ của khách hàng",
      description = "API đơn giản để khách hàng quản lý địa chỉ")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách địa chỉ thành công")
  public ResponseEntity<ApiResponseDto<List<ShippingAddressResponseDto>>> getAllAddressByCustomer(
      @Parameter(description = "ID tài khoản khách hàng") @PathVariable Integer customerId) {

    List<ShippingAddressResponseDto> result = shippingAddressService.getAllByCustomerId(customerId);
    return ResponseUtils.success(result, "Lấy danh sách địa chỉ thành công");
  }

  /** Lấy danh sách địa chỉ có phân trang */
  @GetMapping("/customer/{customerId}/page")
  @Operation(
      summary = "Lấy danh sách địa chỉ có phân trang",
      description = "API có phân trang để quản lý địa chỉ")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách địa chỉ thành công")
  public ResponseEntity<ApiResponseDto<List<ShippingAddressResponseDto>>>
      getAllAddressByCustomerPaged(
          @Parameter(description = "ID tài khoản khách hàng") @PathVariable Integer customerId,
          @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0")
              int page,
          @Parameter(description = "Số địa chỉ trên mỗi trang") @RequestParam(defaultValue = "10")
              int size,
          @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "id") String sort,
          @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "desc")
              String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ShippingAddressResponseDto> result =
        shippingAddressService.getAllByCustomerId(customerId, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách địa chỉ thành công");
  }

  /** Lấy thông tin cơ bản của địa chỉ theo ID. */
  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy thông tin địa chỉ theo ID",
      description = "Lấy thông tin chi tiết của địa chỉ")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Lấy thông tin địa chỉ thành công"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
  })
  public ResponseEntity<ApiResponseDto<ShippingAddressResponseDto>> getAddressById(
      @Parameter(description = "ID địa chỉ") @PathVariable Integer id) {

    ShippingAddressResponseDto result = shippingAddressService.getById(id);
    return ResponseUtils.success(result, "Lấy thông tin địa chỉ thành công");
  }

  /** Xóa địa chỉ */
  @DeleteMapping("/{id}/customer/{customerId}")
  @Operation(summary = "Xóa địa chỉ", description = "Xóa địa chỉ khỏi database")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Xóa địa chỉ thành công"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
  })
  public ResponseEntity<ApiResponseDto<Void>> deleteAddress(
      @Parameter(description = "ID địa chỉ cần xóa") @PathVariable Integer id,
      @Parameter(description = "ID tài khoản khách hàng") @PathVariable Integer customerId) {

    shippingAddressService.delete(id, customerId);
    return ResponseUtils.success(null, "Xóa địa chỉ thành công");
  }

  /** API lấy địa chỉ mặc định */
  @GetMapping("/customer/{customerId}/default")
  @Operation(summary = "Lấy địa chỉ mặc định", description = "Lấy địa chỉ mặc định của khách hàng")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Lấy địa chỉ mặc định thành công"),
    @ApiResponse(responseCode = "404", description = "Tài khoản chưa có địa chỉ mặc định")
  })
  public ResponseEntity<ApiResponseDto<ShippingAddressResponseDto>> getDefaultAddress(
      @Parameter(description = "ID tài khoản") @PathVariable Integer customerId) {
    try {
      ShippingAddressResponseDto result = shippingAddressService.getDefaultAddress(customerId);
      return ResponseUtils.success(result, "Lấy địa chỉ mặc định thành công");
    } catch (ResourceNotFoundException e) {
      return ResponseUtils.error(e.getLocalizedMessage(), HttpStatus.NOT_FOUND);
    }
  }

  /** API cập nhật địa chỉ giao hàng mặc định */
  @PutMapping("/customer/{customerId}/default/{addressId}")
  @Operation(
      summary = "Thay đổi địa chỉ mặc định",
      description = "Thay đổi địa chỉ mặc định cho khách hàng")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Cập nhật địa chỉ mặc định thành công")
  })
  public ResponseEntity<ApiResponseDto<ShippingAddressResponseDto>> setDefaultAddress(
      @Parameter(description = "ID tài khoản") @PathVariable Integer customerId,
      @Parameter(description = "ID địa chỉ ") @PathVariable Integer addressId) {
    ShippingAddressResponseDto updatedAddress =
        shippingAddressService.setDefaultAddress(customerId, addressId);
    return ResponseUtils.success(updatedAddress, "Cập nhật địa chỉ mặc định thành công");
  }

  /** Đếm số lượng địa chỉ của khách hàng */
  @GetMapping("/customer/{customerId}/count")
  @Operation(summary = "Đếm số lượng địa chỉ", description = "Đếm số lượng địa chỉ của khách hàng")
  @ApiResponse(responseCode = "200", description = "Đếm số lượng địa chỉ thành công")
  public ResponseEntity<ApiResponseDto<Long>> countAddressByCustomer(
      @Parameter(description = "ID tài khoản khách hàng") @PathVariable Integer customerId) {

    long count = shippingAddressService.countByCustomerId(customerId);
    return ResponseUtils.success(count, "Đếm số lượng địa chỉ thành công");
  }
}
