package com.example.nikonbe.modules.banner.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.banner.dto.request.BannerCreateDTO;
import com.example.nikonbe.modules.banner.dto.request.BannerUpdateDTO;
import com.example.nikonbe.modules.banner.dto.response.BannerResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BannerService {

  BannerResponseDTO create(BannerCreateDTO dto);

  BannerResponseDTO update(Long id, BannerUpdateDTO dto);

  BannerResponseDTO getById(Long id);

  List<BannerResponseDTO> getAll(Status status, Integer position);

  Page<BannerResponseDTO> getAllPaginated(Status status, Integer position, Pageable pageable);

  void delete(Long id);

  List<BannerResponseDTO> getActiveBannersByPosition(Integer position);
}
