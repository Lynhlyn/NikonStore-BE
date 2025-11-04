package com.example.nikonbe.modules.tag.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.StatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "tag", uniqueConstraints = @UniqueConstraint(columnNames = {"slug"}))
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tag name is required")
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @NotBlank(message = "Slug is required")
  @Column(name = "slug", nullable = false, length = 100)
  private String slug;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Status is required")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;
}
