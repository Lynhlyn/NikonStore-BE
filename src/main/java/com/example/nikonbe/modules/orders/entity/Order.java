package com.example.nikonbe.modules.orders.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_order_customer"))
  private Customer customer;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "discount")
  private BigDecimal discount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "voucher_id", foreignKey = @ForeignKey(name = "fk_order_voucher"))
  private Voucher voucher;

  @Column(name = "payment_method", nullable = false)
  private String paymentMethod;

  @Column(name = "payment_status", nullable = false)
  private String paymentStatus;

  @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
  private String shippingAddress;

  @Column(name = "shipping_fee", nullable = false)
  private BigDecimal shippingFee;

  @Column(name = "order_type", nullable = false)
  private String orderType;

  @Column(name = "status")
  @Enumerated(EnumType.ORDINAL)
  private Status status;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String note;

  @Column(name = "order_code", nullable = false)
  private String trackingNumber;

  @Column(name = "recipient_name", length = 255)
  private String recipientName;

  @Column(name = "recipient_phone", length = 20)
  private String recipientPhone;

  @Column(name = "recipient_email", length = 255)
  private String recipientEmail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "staff_id", foreignKey = @ForeignKey(name = "fk_order_staff"))
  private Staff staff;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderDetail> orderDetails;
}
