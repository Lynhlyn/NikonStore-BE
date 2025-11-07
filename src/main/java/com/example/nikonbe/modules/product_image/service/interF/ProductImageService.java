package com.example.nikonbe.modules.product_image.service.interF;

import com.example.nikonbe.modules.product_image.dto.request.ProductImageCreateDTO;
import com.example.nikonbe.modules.product_image.dto.request.ProductImageUpdateDTO;
import com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO;
import java.util.List;

public interface ProductImageService {

  ProductImageResponseDTO create(ProductImageCreateDTO dto);

  ProductImageResponseDTO update(Integer id, ProductImageUpdateDTO dto);

  ProductImageResponseDTO getById(Integer id);

  List<ProductImageResponseDTO> getByProductId(Integer productId);

  void delete(Integer id);

  void deleteByProductId(Integer productId);
}
