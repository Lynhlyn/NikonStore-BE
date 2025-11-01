package com.example.nikonbe.modules.customervoucher.entity;

import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "customer_voucher")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerVoucher {
  @EmbeddedId private CustomerVoucherId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("customerId")
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("voucherId")
  @JoinColumn(name = "voucher_id")
  private Voucher voucher;

  @Column(name = "used_at")
  private LocalDateTime usedAt;
}
