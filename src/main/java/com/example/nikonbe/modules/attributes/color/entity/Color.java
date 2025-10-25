package com.example.nikonbe.modules.attributes.color.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.StatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "color")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Color extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Color name is required")
  @Column(unique = true, nullable = false)
  private String name;

  @Column(name = "hex_code", length = 20)
  private String hexCode;

  @NotNull(message = "Status is required")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;
}
