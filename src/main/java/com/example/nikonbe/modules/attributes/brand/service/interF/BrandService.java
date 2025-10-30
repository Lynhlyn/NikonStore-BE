package com.example.nikonbe.modules.attributes.brand.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandCreateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.request.BrandUpdateDTO;
import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrandService {
  BrandResponseDTO create(BrandCreateDTO dto);

  BrandResponseDTO update(Integer id, BrandUpdateDTO dto);

  BrandResponseDTO getById(Integer id);

  List<BrandResponseDTO> getAll();

  Page<BrandResponseDTO> getAllPaginated(Pageable pageable);

  List<BrandResponseDTO> getAllByStatus(Status status);

  Page<BrandResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByName(String name);
}
