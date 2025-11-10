package com.example.nikonbe.modules.order_history.entity;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.staff.entity.Staff;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "order_history")
public class OrderHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(name = "change_by_type", nullable = false)
  private UserRole changeByType;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status_before")
  private Status statusBefore;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status_after")
  private Status statusAfter;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "order_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_order_history_order"))
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "change_by_customer_id",
      nullable = true,
      foreignKey = @ForeignKey(name = "fk_order_history_customer"))
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "change_by_staff_id",
      nullable = true,
      foreignKey = @ForeignKey(name = "fk_order_history_staff"))
  private Staff staff;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
