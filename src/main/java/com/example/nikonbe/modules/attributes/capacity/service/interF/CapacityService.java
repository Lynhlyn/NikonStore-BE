package com.example.nikonbe.modules.attributes.capacity.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityCreateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityUpdateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CapacityService {
  CapacityResponseDTO create(CapacityCreateDTO dto);

  CapacityResponseDTO update(Integer id, CapacityUpdateDTO dto);

  CapacityResponseDTO getById(Integer id);

  List<CapacityResponseDTO> getAll();

  List<CapacityResponseDTO> getAllByStatus(Status status);

  Page<CapacityResponseDTO> getAllPaginated(Pageable pageable);

  Page<CapacityResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByName(String name);

  List<CapacityResponseDTO> search(String keyword);

  Page<CapacityResponseDTO> searchPaginated(String keyword, Pageable pageable);
}
