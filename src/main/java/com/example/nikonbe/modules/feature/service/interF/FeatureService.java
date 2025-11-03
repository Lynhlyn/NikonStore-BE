package com.example.nikonbe.modules.feature.service.interF;

import com.example.nikonbe.modules.feature.dto.request.FeatureCreateDTO;
import com.example.nikonbe.modules.feature.dto.request.FeatureUpdateDTO;
import com.example.nikonbe.modules.feature.dto.response.FeatureResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeatureService {

  FeatureResponseDTO create(FeatureCreateDTO dto);

  FeatureResponseDTO update(Integer id, FeatureUpdateDTO dto);

  FeatureResponseDTO getById(Integer id);

  List<FeatureResponseDTO> getAll(String name, String featureGroup);

  Page<FeatureResponseDTO> getAllPaginated(String name, String featureGroup, Pageable pageable);

  void delete(Integer id);
}
