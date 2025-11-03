package com.example.nikonbe.modules.feature.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.feature.dto.request.FeatureCreateDTO;
import com.example.nikonbe.modules.feature.dto.request.FeatureUpdateDTO;
import com.example.nikonbe.modules.feature.dto.response.FeatureResponseDTO;
import com.example.nikonbe.modules.feature.entity.Feature;
import com.example.nikonbe.modules.feature.mapper.FeatureMapper;
import com.example.nikonbe.modules.feature.repository.FeatureRepository;
import com.example.nikonbe.modules.feature.service.interF.FeatureService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeatureServiceImpl implements FeatureService {

  private final FeatureRepository featureRepository;
  private final FeatureMapper featureMapper;

  @Override
  @Transactional
  public FeatureResponseDTO create(FeatureCreateDTO dto) {
    validateCreateRequest(dto);

    Feature feature = featureMapper.toEntity(dto);
    Feature savedFeature = featureRepository.save(feature);
    return featureMapper.toDto(savedFeature);
  }

  @Override
  @Transactional
  public FeatureResponseDTO update(Integer id, FeatureUpdateDTO dto) {
    Feature feature = findFeatureById(id);
    validateUpdateRequest(dto, feature);
    featureMapper.updateEntityFromDto(dto, feature);
    Feature updatedFeature = featureRepository.save(feature);
    return featureMapper.toDto(updatedFeature);
  }

  @Override
  public FeatureResponseDTO getById(Integer id) {
    Feature feature = findFeatureById(id);
    return featureMapper.toDto(feature);
  }

  @Override
  public List<FeatureResponseDTO> getAll(String name, String featureGroup) {
    List<Feature> features = featureRepository.findAllByFilters(name, featureGroup);
    return featureMapper.toDtoList(features);
  }

  @Override
  public Page<FeatureResponseDTO> getAllPaginated(
      String name, String featureGroup, Pageable pageable) {
    Page<Feature> features = featureRepository.findAllPaginated(name, featureGroup, pageable);
    return features.map(featureMapper::toDto);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    Feature feature = findFeatureById(id);
    featureRepository.delete(feature);
  }

  private Feature findFeatureById(Integer id) {
    return featureRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Feature", "id", id));
  }

  private void validateCreateRequest(FeatureCreateDTO dto) {
    if (featureRepository.existsByName(dto.getName())) {
      throw new ValidationException("A feature with this name already exists.");
    }
  }

  private void validateUpdateRequest(FeatureUpdateDTO dto, Feature feature) {
    if (!feature.getName().equals(dto.getName()) && featureRepository.existsByName(dto.getName())) {
      throw new ValidationException("A feature with this name already exists.");
    }
  }
}
