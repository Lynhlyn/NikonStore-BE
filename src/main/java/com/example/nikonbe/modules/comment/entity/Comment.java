package com.example.nikonbe.modules.comment.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.blog.entity.Blog;
import com.example.nikonbe.modules.customer.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "comments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "blog_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_comments_blog"))
  private Blog blog;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "customer_id",
      foreignKey = @ForeignKey(name = "fk_comments_customer"))
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "staff_id",
      foreignKey = @ForeignKey(name = "fk_comments_staff"))
  private com.example.nikonbe.modules.staff.entity.Staff staff;

  @Column(name = "user_comment")
  private String userComment;

  @NotBlank(message = "Nội dung bình luận không được để trống")
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_comments_parent"))
  private Comment parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Comment> replies;

  @NotNull
  @Column(nullable = false)
  @Builder.Default
  private Boolean status = false;
}


