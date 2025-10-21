package com.example.nikonbe.modules.attributes.capacity.entity;

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
@Table(name = "capacity")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Capacity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Capacity name is required")
  @Column(unique = true, nullable = false)
  private String name;

  @NotNull(message = "Status is required")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;

  @Column private Double liters;
}
