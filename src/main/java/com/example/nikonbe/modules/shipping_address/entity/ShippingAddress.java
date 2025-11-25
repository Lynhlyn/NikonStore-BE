package com.example.nikonbe.modules.shipping_address.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.customer.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "shipping_address")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddress extends BaseEntity {

  /** ID tự động tăng của địa chỉ */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  /** Customer chủ sở hữu của địa chỉ */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  /** Tên người nhận */
  @Size(max = 255)
  @NotNull
  @Column(name = "recipient_name", nullable = false)
  private String recipientName;

  /** Số điện thoại người nhận */
  @Size(max = 20)
  @NotNull
  @Column(name = "recipient_phone_number", nullable = false, length = 20)
  private String recipientPhoneNumber;

  /** Tỉnh/Thành */
  @Size(max = 100)
  @NotNull
  @Column(name = "province", nullable = false, length = 100)
  private String province;

  /** Quận/Huyện */
  @Size(max = 100)
  @NotNull
  @Column(name = "district", nullable = false, length = 100)
  private String district;

  /** Xã/Phường */
  @Size(max = 100)
  @NotNull
  @Column(name = "ward", nullable = false, length = 100)
  private String ward;

  /** Địa chỉ chi tiết */
  @Size(max = 255)
  @NotNull
  @Column(name = "detailed_address", nullable = false)
  private String detailedAddress;

  /** Địa chỉ mặc định */
  @ColumnDefault("0")
  @Builder.Default
  @Column(name = "is_default")
  private Boolean isDefault = false;
}
