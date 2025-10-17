package com.example.nikonbe.modules.attributes.category.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "category")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Category name is required")
  @Column(nullable = false, unique = true)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "parent_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_category_parent"),
      nullable = true)
  private Category parent;

  @Column(columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Status is required")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;
}
