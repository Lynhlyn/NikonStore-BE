package com.example.nikonbe.modules.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Table(name = "review_image")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "review_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_review_image_review"))
  private Review review;

  @NotBlank
  @Column(name = "image_url", nullable = false, length = 500)
  private String imageUrl;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
