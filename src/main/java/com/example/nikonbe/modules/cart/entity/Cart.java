package com.example.nikonbe.modules.cart.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.cart_detail.entity.CartDetail;
import com.example.nikonbe.modules.customer.entity.Customer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "cart")
public class Cart extends BaseEntity {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_cart_customer"))
  private Customer customer;

  @Column(name = "cookie_id", length = 255)
  private String cookieId;

  @Column(name = "expire_at")
  private LocalDateTime expireAt;

  @OneToMany(
      mappedBy = "cart",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<CartDetail> cartDetails = new ArrayList<>();
}
