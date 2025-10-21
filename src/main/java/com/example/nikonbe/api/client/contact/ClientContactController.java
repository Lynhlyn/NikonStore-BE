package com.example.nikonbe.api.client.contact;

import com.example.nikonbe.modules.contact.dto.request.ContactCreateDTO;
import com.example.nikonbe.modules.contact.dto.response.ContactResponseDTO;
import com.example.nikonbe.modules.contact.service.interF.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}/contact")
@RequiredArgsConstructor
@Tag(name = "Client - Contact", description = "API contact cho người dùng ")
public class ClientContactController {
  private final ContactService contactService;

  @PostMapping
  @Operation(summary = "Client tạo mới contact")
  public ContactResponseDTO create(@Valid @RequestBody ContactCreateDTO dto) {
    return contactService.create(dto);
  }
}
