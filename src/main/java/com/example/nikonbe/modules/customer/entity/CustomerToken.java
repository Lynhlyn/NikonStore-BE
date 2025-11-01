package com.example.nikonbe.modules.customer.entity;

import com.example.nikonbe.common.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entity lưu trữ thông tin token. */
@Getter
@Setter
@Entity
@Table(name = "customer_token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerToken extends BaseEntity {

  /** ID tự tăng của token. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  /** Thông tin token để truy cập. */
  @Size(max = 255)
  @NotNull
  @Column(name = "access_token", nullable = false)
  private String accessToken;

  /** mã làm mới token. */
  @Size(max = 255)
  @NotNull
  @Column(name = "refresh_token", nullable = false)
  private String refreshToken;

  /** mã đặt lại mật khẩu */
  @Size(max = 50)
  @Column(name = "token_reset", length = 50)
  private String tokenReset;

  /** Thời gian hiết hạn. */
  @NotNull
  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;
}
