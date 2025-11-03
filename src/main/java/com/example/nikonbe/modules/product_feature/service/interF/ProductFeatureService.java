package com.example.nikonbe.modules.product_feature.service.interF;

import com.example.nikonbe.modules.product_feature.dto.request.ProductFeatureCreateDTO;
import com.example.nikonbe.modules.product_feature.dto.request.ProductFeatureUpdateDTO;
import com.example.nikonbe.modules.product_feature.dto.response.ProductFeatureResponseDTO;
import java.util.List;

public interface ProductFeatureService {

  ProductFeatureResponseDTO addFeatureToProduct(Integer productId, ProductFeatureCreateDTO dto);

  List<ProductFeatureResponseDTO> updateProductFeatures(
      Integer productId, ProductFeatureUpdateDTO dto);

  List<ProductFeatureResponseDTO> getByProductId(Integer productId);

  void removeFeatureFromProduct(Integer productId, Integer featureId);

  void removeAllFeaturesFromProduct(Integer productId);
}
