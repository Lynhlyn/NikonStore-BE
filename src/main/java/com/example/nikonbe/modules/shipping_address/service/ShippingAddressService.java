package com.example.nikonbe.modules.shipping_address.service;

import com.example.nikonbe.modules.shipping_address.dto.request.CreateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.request.UpdateShippingAddressDTO;
import com.example.nikonbe.modules.shipping_address.dto.response.ShippingAddressResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for {@link com.example.nikonbe.modules.shipping_address.entity.ShippingAddress}
 *
 * <p>Interface định nghĩa các chức năng dịch vụ liên quan đến địa chỉ giao hàng
 */
public interface ShippingAddressService {
  /**
   * Thêm địa chỉ mới
   *
   * @param dto dữ liệu tạo địa chỉ
   * @return thông tin địa chỉ mới
   */
  ShippingAddressResponseDto create(CreateShippingAddressDTO dto);

  /**
   * Cập nhật thông tin địa chỉ theo ID
   *
   * @param id ID địa chỉ cần cập nhật
   * @param dto dữ liệu cập nhật địa chỉ
   * @return thông tin địa chỉ sau khi cập nhật
   */
  ShippingAddressResponseDto update(Integer id, UpdateShippingAddressDTO dto);

  /**
   * Lấy thông tin chi tiết địa chỉ theo ID
   *
   * @param id ID địa chỉ
   * @return thông tin địa chỉ
   */
  ShippingAddressResponseDto getById(Integer id);

  /**
   * Lấy tất cả địa chỉ của khách hàng
   *
   * @param customerId ID khách hàng
   * @return danh sách địa chỉ
   */
  List<ShippingAddressResponseDto> getAllByCustomerId(Integer customerId);

  /**
   * Lấy danh sách địa chỉ theo ID khách hàng, có phân trang
   *
   * @param customerId ID khách hàng
   * @param pageable thông tin phân trang
   * @return danh sách phân trang địa chỉ phù hợp
   */
  Page<ShippingAddressResponseDto> getAllByCustomerId(Integer customerId, Pageable pageable);

  /**
   * Xóa địa chỉ theo ID
   *
   * @param id ID địa chỉ cần xóa
   * @param customerId ID khách hàng (để kiểm tra quyền)
   */
  void delete(Integer id, Integer customerId);

  /**
   * Đặt địa chỉ làm mặc định
   *
   * @param customerId ID khách hàng
   * @param addressId ID địa chỉ
   * @return thông tin địa chỉ sau khi cập nhật
   */
  ShippingAddressResponseDto setDefaultAddress(Integer customerId, Integer addressId);

  /**
   * Lấy địa chỉ mặc định của khách hàng
   *
   * @param customerId ID khách hàng
   * @return địa chỉ mặc định
   */
  ShippingAddressResponseDto getDefaultAddress(Integer customerId);

  /**
   * Kiểm tra khách hàng có quyền truy cập địa chỉ không
   *
   * @param addressId ID địa chỉ
   * @param customerId ID khách hàng
   * @return true nếu có quyền
   */
  boolean hasAccessToAddress(Integer addressId, Integer customerId);

  /**
   * Đếm số lượng địa chỉ của khách hàng
   *
   * @param customerId ID khách hàng
   * @return số lượng địa chỉ
   */
  long countByCustomerId(Integer customerId);
}
