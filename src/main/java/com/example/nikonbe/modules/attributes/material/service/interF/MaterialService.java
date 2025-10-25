package com.example.nikonbe.modules.attributes.material.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialCreateDTO;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialUpdateDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialService {
  MaterialResponseDTO create(MaterialCreateDTO dto);

  MaterialResponseDTO update(Integer id, MaterialUpdateDTO dto);

  MaterialResponseDTO getById(Integer id);

  List<MaterialResponseDTO> getAll();

  List<MaterialResponseDTO> getAllByStatus(Status status);

  Page<MaterialResponseDTO> getAllPaginated(Pageable pageable);

  Page<MaterialResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByName(String name);

  List<MaterialResponseDTO> search(String keyword);

  Page<MaterialResponseDTO> searchPaginated(String keyword, Pageable pageable);
}
