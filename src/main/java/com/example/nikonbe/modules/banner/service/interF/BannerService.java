package com.example.nikonbe.modules.banner.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.banner.dto.request.BannerCreateDTO;
import com.example.nikonbe.modules.banner.dto.request.BannerUpdateDTO;
import com.example.nikonbe.modules.banner.dto.response.BannerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BannerService {
  
  BannerResponseDTO create(BannerCreateDTO dto);
  
  BannerResponseDTO update(Long id, BannerUpdateDTO dto);
  
  BannerResponseDTO getById(Long id);
  
  List<BannerResponseDTO> getAll(Status status, String position);
  
  Page<BannerResponseDTO> getAllPaginated(Status status, String position, Pageable pageable);
  
  void delete(Long id);
  
  List<BannerResponseDTO> getActiveBannersByPosition(String position);
}
