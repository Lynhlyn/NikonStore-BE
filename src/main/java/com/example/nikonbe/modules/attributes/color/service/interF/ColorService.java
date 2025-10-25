package com.example.nikonbe.modules.attributes.color.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorCreateDTO;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorUpdateDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColorService {
  ColorResponseDTO create(ColorCreateDTO dto);

  ColorResponseDTO update(Integer id, ColorUpdateDTO dto);

  ColorResponseDTO getById(Integer id);

  List<ColorResponseDTO> getAll(String name, Status status);

  Page<ColorResponseDTO> getAllPaginated(String name, Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByName(String name);
}
