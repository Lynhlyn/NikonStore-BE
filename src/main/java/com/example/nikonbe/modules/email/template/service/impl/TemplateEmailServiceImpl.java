package com.example.nikonbe.modules.email.template.service.impl;

import com.example.nikonbe.common.enums.EmailAction;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailCreateDTO;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailUpdateDTO;
import com.example.nikonbe.modules.email.template.dto.response.TemplateEmailResponseDTO;
import com.example.nikonbe.modules.email.template.entity.TemplateEmail;
import com.example.nikonbe.modules.email.template.mapper.TemplateEmailMapper;
import com.example.nikonbe.modules.email.template.repository.TemplateEmailRepository;
import com.example.nikonbe.modules.email.template.service.interF.TemplateEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateEmailServiceImpl implements TemplateEmailService {

  private final TemplateEmailRepository templateEmailRepository;
  private final TemplateEmailMapper templateEmailMapper;
  private final JavaMailSender javaMailSender;

  @Value("${mail.from.address}")
  private String fromAddress;

  @Value("${mail.from.name}")
  private String fromName;

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

  @Transactional
  @Override
  public TemplateEmailResponseDTO create(TemplateEmailCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (templateEmailRepository.existsByAction(dto.getAction())) {
      errors.put("action", "Template with this action already exists");
    }

    if (dto.getAction() == null) {
      errors.put("action", "Action is required");
    }

    if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
      errors.put("subject", "Subject is required");
    }

    if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
      errors.put("content", "Content is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    TemplateEmail templateEmail = templateEmailMapper.toEntity(dto);
    TemplateEmail savedTemplateEmail = templateEmailRepository.save(templateEmail);
    return templateEmailMapper.toDto(savedTemplateEmail);
  }

  @Transactional
  @Override
  public TemplateEmailResponseDTO update(Integer id, TemplateEmailUpdateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    TemplateEmail templateEmail =
        templateEmailRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TemplateEmail", "id", id));

    if (templateEmailRepository.existsByActionAndIdNot(dto.getAction(), id)) {
      errors.put("action", "Template with this action already exists");
    }

    if (dto.getAction() == null) {
      errors.put("action", "Action is required");
    }

    if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
      errors.put("subject", "Subject is required");
    }

    if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
      errors.put("content", "Content is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    templateEmailMapper.updateEntityFromDto(dto, templateEmail);
    TemplateEmail updatedTemplateEmail = templateEmailRepository.save(templateEmail);
    return templateEmailMapper.toDto(updatedTemplateEmail);
  }

  @Transactional(readOnly = true)
  @Override
  public TemplateEmailResponseDTO getById(Integer id) {
    TemplateEmail templateEmail =
        templateEmailRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TemplateEmail", "id", id));
    return templateEmailMapper.toDto(templateEmail);
  }

  @Transactional(readOnly = true)
  @Override
  public TemplateEmailResponseDTO getByAction(EmailAction action) {
    TemplateEmail templateEmail =
        templateEmailRepository
            .findByAction(action)
            .orElseThrow(
                () -> new ResourceNotFoundException("TemplateEmail", "action", action.getValue()));
    return templateEmailMapper.toDto(templateEmail);
  }

  @Transactional(readOnly = true)
  @Override
  public List<TemplateEmailResponseDTO> getAll() {
    List<TemplateEmail> templates = templateEmailRepository.findAll();
    return templateEmailMapper.toDtoList(templates);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<TemplateEmailResponseDTO> getAllPaginated(Pageable pageable) {
    Page<TemplateEmail> templatePage = templateEmailRepository.findAll(pageable);
    return templatePage.map(templateEmailMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    TemplateEmail templateEmail =
        templateEmailRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TemplateEmail", "id", id));
    templateEmailRepository.delete(templateEmail);
  }

  @Override
  public boolean existsByAction(EmailAction action) {
    return templateEmailRepository.existsByAction(action);
  }

  @Override
  public void sendTemplateEmail(EmailAction action, String toEmail, Map<String, Object> data) {
    sendTemplateEmail(action, toEmail, null, data);
  }

  @Override
  public void sendTemplateEmail(
      EmailAction action, String toEmail, String customSubject, Map<String, Object> data) {
    try {
      TemplateEmail template =
          templateEmailRepository
              .findByAction(action)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException("TemplateEmail", "action", action.getValue()));

      String subject =
          customSubject != null ? customSubject : renderTemplate(template.getSubject(), data);
      String content = renderTemplate(template.getContent(), data);

      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromAddress, fromName);
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(content, true);

      javaMailSender.send(message);

      log.info(
          "Email sent successfully to {} with action {} from {} ({})",
          toEmail,
          action.getValue(),
          fromName,
          fromAddress);

    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error(
          "Failed to send email to {} with action {}: {}",
          toEmail,
          action.getValue(),
          e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }

  @Override
  public List<EmailAction> getAllAvailableActions() {
    return Arrays.asList(EmailAction.values());
  }

  private String renderTemplate(String template, Map<String, Object> data) {
    if (template == null || data == null) {
      return template;
    }

    String result = template;
    Matcher matcher = VARIABLE_PATTERN.matcher(template);

    while (matcher.find()) {
      String variable = matcher.group(1).trim();
      Object value = data.get(variable);
      if (value != null) {
        result = result.replace("{{" + variable + "}}", value.toString());
      }
    }

    return result;
  }
}
