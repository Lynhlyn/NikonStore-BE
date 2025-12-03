package com.example.nikonbe.modules.blog.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import com.example.nikonbe.modules.staff.entity.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "blog")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Blog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tiêu đề không được để trống")
  @Column(nullable = false)
  private String title;

  @NotBlank(message = "Slug không được để trống")
  @Column(nullable = false, unique = true)
  private String slug;

  @Column(columnDefinition = "TEXT")
  private String summary;

  @NotBlank(message = "Nội dung không được để trống")
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "staff_id", foreignKey = @ForeignKey(name = "fk_blog_staff"))
  private Staff staff;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_blog_category"))
  private ContentCategory category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_blog_tag"))
  private ContentTag tag;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isPublished = false;

  @Column(nullable = false)
  @Builder.Default
  private Integer viewCount = 0;
}


