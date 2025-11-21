package com.example.nikonbe.modules.product.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product.dto.request.ProductCreateDTO;
import com.example.nikonbe.modules.product.dto.request.ProductUpdateDTO;
import com.example.nikonbe.modules.product.dto.response.ProductDetailFullResponseDTO;
import com.example.nikonbe.modules.product.dto.response.ProductListingResponseDTO;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  ProductResponseDTO create(ProductCreateDTO dto);

  ProductResponseDTO update(Integer id, ProductUpdateDTO dto);

  ProductResponseDTO getById(Integer id);

  Page<ProductResponseDTO> getAll(
      String keyword,
      Status status,
      Integer categoryId,
      Integer brandId,
      Integer materialId,
      Integer strapTypeId,
      Pageable pageable);

  void delete(Integer id);

  Page<ProductListingResponseDTO> getProductListings(
      String keyword,
      List<Integer> brandIds,
      List<Integer> strapTypeIds,
      List<Integer> materialIds,
      List<Integer> categoryIds,
      List<Integer> colorIds,
      List<Integer> capacityIds,
      List<Integer> tagIds,
      List<Integer> featureIds,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Boolean hasPromotion,
      Pageable pageable);

  ProductDetailFullResponseDTO getProductDetail(Integer productId);
}
