package com.example.nikonbe.modules.attributes.color.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceAlreadyExistsException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorCreateDTO;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorUpdateDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import com.example.nikonbe.modules.attributes.color.entity.Color;
import com.example.nikonbe.modules.attributes.color.mapper.ColorMapper;
import com.example.nikonbe.modules.attributes.color.repository.ColorRepository;
import com.example.nikonbe.modules.attributes.color.service.interF.ColorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {
  private final ColorRepository colorRepository;
  private final ColorMapper colorMapper;

  @Transactional
  @Override
  public ColorResponseDTO create(ColorCreateDTO dto) {
    if (colorRepository.existsByName(dto.getName())) {
      throw new ResourceAlreadyExistsException("Color", "name", dto.getName());
    }

    Color color = colorMapper.toEntity(dto);
    Color savedColor = colorRepository.save(color);
    return colorMapper.toDto(savedColor);
  }

  @Transactional
  @Override
  public ColorResponseDTO update(Integer id, ColorUpdateDTO dto) {
    Color color =
        colorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Color", "id", id));

    if (colorRepository.existsByNameAndIdNot(dto.getName(), id)) {
      throw new ResourceAlreadyExistsException("Color", "name", dto.getName());
    }

    colorMapper.updateEntityFromDto(dto, color);
    Color updatedColor = colorRepository.save(color);

    return colorMapper.toDto(updatedColor);
  }

  @Transactional(readOnly = true)
  @Override
  public ColorResponseDTO getById(Integer id) {
    Color color =
        colorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Color", "id", id));
    return colorMapper.toDto(color);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ColorResponseDTO> getAll(String name, Status status) {
    List<Color> colors;
    if (name != null && !name.trim().isEmpty() && status != null) {
      colors = colorRepository.findByNameContainingIgnoreCaseAndStatus(name, status);
    } else if (name != null && !name.trim().isEmpty()) {
      colors = colorRepository.findByNameContainingIgnoreCase(name);
    } else if (status != null) {
      colors = colorRepository.findByStatus(status);
    } else {
      colors = colorRepository.findAll();
    }
    return colorMapper.toDtoList(colors);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ColorResponseDTO> getAllPaginated(String name, Status status, Pageable pageable) {
    Page<Color> colorPage;
    if (name != null && !name.trim().isEmpty() && status != null) {
      colorPage = colorRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
    } else if (name != null && !name.trim().isEmpty()) {
      colorPage = colorRepository.findByNameContainingIgnoreCase(name, pageable);
    } else if (status != null) {
      colorPage = colorRepository.findByStatus(status, pageable);
    } else {
      colorPage = colorRepository.findAll(pageable);
    }
    return colorPage.map(colorMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    Color color =
        colorRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Color", "id", id));

    colorRepository.delete(color);
  }

  @Override
  public boolean existsByName(String name) {
    return colorRepository.existsByName(name);
  }
}
