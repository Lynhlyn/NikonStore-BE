package com.example.nikonbe.modules.customervoucher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CustomerVoucherId implements Serializable {
  @Column(name = "customer_id")
  private Integer customerId;

  @Column(name = "voucher_id")
  private Long voucherId;
}
