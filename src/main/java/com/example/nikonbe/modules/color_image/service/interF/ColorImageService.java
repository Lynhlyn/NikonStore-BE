package com.example.nikonbe.modules.color_image.service.interF;

import com.example.nikonbe.modules.color_image.dto.request.ColorImageCreateDTO;
import com.example.nikonbe.modules.color_image.dto.request.ColorImageUpdateDTO;
import com.example.nikonbe.modules.color_image.dto.response.ColorImageResponseDTO;
import java.util.List;

public interface ColorImageService {

  ColorImageResponseDTO create(ColorImageCreateDTO dto);

  ColorImageResponseDTO update(Integer id, ColorImageUpdateDTO dto);

  ColorImageResponseDTO getById(Integer id);

  List<ColorImageResponseDTO> getAll();

  List<ColorImageResponseDTO> getByProductId(Integer productId);

  ColorImageResponseDTO getByProductIdAndColorId(Integer productId, Integer colorId);

  void delete(Integer id);

  void deleteByProductAndColor(Integer productId, Integer colorId);
}
