package com.example.nikonbe.modules.content_tag.service.interF;

import com.example.nikonbe.modules.content_tag.dto.request.ContentTagCreateDTO;
import com.example.nikonbe.modules.content_tag.dto.request.ContentTagUpdateDTO;
import com.example.nikonbe.modules.content_tag.dto.response.ContentTagResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentTagService {
  ContentTagResponseDTO create(ContentTagCreateDTO dto);

  ContentTagResponseDTO update(Integer id, ContentTagUpdateDTO dto);

  ContentTagResponseDTO getById(Integer id);

  List<ContentTagResponseDTO> getAll(String name, String slug, String type);

  Page<ContentTagResponseDTO> getAllPaginated(
      String name, String slug, String type, Pageable pageable);

  void delete(Integer id);
}
