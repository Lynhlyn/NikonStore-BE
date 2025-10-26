package com.example.nikonbe.modules.attributes.strape_type.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeCreateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeUpdateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StrapTypeService {
  StrapTypeResponseDTO create(StrapTypeCreateDTO dto);

  StrapTypeResponseDTO update(Integer id, StrapTypeUpdateDTO dto);

  StrapTypeResponseDTO getById(Integer id);

  List<StrapTypeResponseDTO> getAll();

  List<StrapTypeResponseDTO> getAllByStatus(Status status);

  Page<StrapTypeResponseDTO> getAllPaginated(Pageable pageable);

  Page<StrapTypeResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByName(String name);

  // Search
  List<StrapTypeResponseDTO> search(String keyword);

  Page<StrapTypeResponseDTO> searchPaginated(String keyword, Pageable pageable);
}
