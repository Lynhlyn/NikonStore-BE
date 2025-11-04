package com.example.nikonbe.modules.tag.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.tag.dto.request.TagCreateDTO;
import com.example.nikonbe.modules.tag.dto.request.TagUpdateDTO;
import com.example.nikonbe.modules.tag.dto.response.TagResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

  TagResponseDTO create(TagCreateDTO dto);

  TagResponseDTO update(Integer id, TagUpdateDTO dto);

  TagResponseDTO getById(Integer id);

  List<TagResponseDTO> getAll(String name, String slug, Status status);

  Page<TagResponseDTO> getAllPaginated(String name, String slug, Status status, Pageable pageable);

  void delete(Integer id);
}
