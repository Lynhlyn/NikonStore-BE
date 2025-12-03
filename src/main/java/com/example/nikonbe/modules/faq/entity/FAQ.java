package com.example.nikonbe.modules.faq.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "faq")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQ extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Câu hỏi không được để trống")
  @Column(columnDefinition = "TEXT", nullable = false)
  private String question;

  @NotBlank(message = "Câu trả lời không được để trống")
  @Column(columnDefinition = "TEXT", nullable = false)
  private String answer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "category_id",
      foreignKey = @ForeignKey(name = "fk_faq_category"))
  private ContentCategory category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_faq_tag"))
  private ContentTag tag;

  @NotNull
  @Column(nullable = false)
  @Builder.Default
  private Boolean status = true;
}


