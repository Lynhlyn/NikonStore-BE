package com.example.nikonbe.modules.product.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  ProductResponseDTO create(ProductCreateDTO dto);

  ProductResponseDTO update(Integer id, ProductUpdateDTO dto);

  ProductResponseDTO getById(Integer id);

  Page<ProductResponseDTO> getAll(
      Status status, Integer categoryId, Integer brandId, Pageable pageable);

  void delete(Integer id);
}
