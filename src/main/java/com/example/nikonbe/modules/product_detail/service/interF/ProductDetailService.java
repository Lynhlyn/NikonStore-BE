package com.example.nikonbe.modules.product_detail.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailCreateDTO;
import com.example.nikonbe.modules.product_detail.dto.request.ProductDetailUpdateDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductDetailService {

  ProductDetailResponseDTO create(ProductDetailCreateDTO dto);

  ProductDetailResponseDTO update(Integer id, ProductDetailUpdateDTO dto);

  ProductDetailResponseDTO getById(Integer id);

  Page<ProductDetailResponseDTO> getAll(
      String sku,
      Status status,
      Integer productId,
      Integer colorId,
      Integer capacityId,
      Pageable pageable);

  void delete(Integer id);
}
