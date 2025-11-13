package com.example.nikonbe.modules.email.template.service.interF;

import com.example.nikonbe.common.enums.EmailAction;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailCreateDTO;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailUpdateDTO;
import com.example.nikonbe.modules.email.template.dto.response.TemplateEmailResponseDTO;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplateEmailService {

  TemplateEmailResponseDTO create(TemplateEmailCreateDTO dto);

  TemplateEmailResponseDTO update(Integer id, TemplateEmailUpdateDTO dto);

  TemplateEmailResponseDTO getById(Integer id);

  TemplateEmailResponseDTO getByAction(EmailAction action);

  List<TemplateEmailResponseDTO> getAll();

  Page<TemplateEmailResponseDTO> getAllPaginated(Pageable pageable);

  void delete(Integer id);

  boolean existsByAction(EmailAction action);

  void sendTemplateEmail(EmailAction action, String toEmail, Map<String, Object> data);

  void sendTemplateEmail(
      EmailAction action, String toEmail, String customSubject, Map<String, Object> data);

  List<EmailAction> getAllAvailableActions();
}
