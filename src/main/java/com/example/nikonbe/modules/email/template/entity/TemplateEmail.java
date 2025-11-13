package com.example.nikonbe.modules.email.template.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.EmailAction;
import com.example.nikonbe.common.utils.EmailActionConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "template_email")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateEmail extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull(message = "Action is required")
  @Convert(converter = EmailActionConverter.class)
  @Column(unique = true, nullable = false, length = 100)
  private EmailAction action;

  @NotBlank(message = "Subject is required")
  @Column(nullable = false)
  private String subject;

  @NotBlank(message = "Content is required")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
}
