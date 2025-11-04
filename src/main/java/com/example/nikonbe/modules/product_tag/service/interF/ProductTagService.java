package com.example.nikonbe.modules.product_tag.service.interF;

import com.example.nikonbe.modules.product_tag.dto.request.ProductTagCreateDTO;
import com.example.nikonbe.modules.product_tag.dto.request.ProductTagUpdateDTO;
import com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO;
import java.util.List;

public interface ProductTagService {

  ProductTagResponseDTO addTag(Integer productId, ProductTagCreateDTO dto);

  List<ProductTagResponseDTO> updateTags(Integer productId, ProductTagUpdateDTO dto);

  List<ProductTagResponseDTO> getByProductId(Integer productId);

  List<ProductTagResponseDTO> getByTagId(Integer tagId);

  void removeTag(Integer productId, Integer tagId);

  void removeAllTags(Integer productId);
}
