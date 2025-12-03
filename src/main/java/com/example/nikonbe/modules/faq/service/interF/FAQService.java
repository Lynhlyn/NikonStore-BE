package com.example.nikonbe.modules.faq.service.interF;

import com.example.nikonbe.modules.faq.dto.request.FAQCreateDTO;
import com.example.nikonbe.modules.faq.dto.request.FAQUpdateDTO;
import com.example.nikonbe.modules.faq.dto.response.FAQResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FAQService {

  FAQResponseDTO create(FAQCreateDTO dto);

  FAQResponseDTO update(Integer id, FAQUpdateDTO dto);

  FAQResponseDTO getById(Integer id);

  Page<FAQResponseDTO> getAll(Integer categoryId, Integer tagId, Boolean status, Pageable pageable);

  void delete(Integer id);

  FAQResponseDTO updateStatus(Integer id, Boolean status);
}


