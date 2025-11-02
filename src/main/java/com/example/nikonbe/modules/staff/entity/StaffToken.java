package com.example.nikonbe.modules.staff.entity;

import com.example.nikonbe.common.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "staff_token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffToken extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull(message = "Staff không được để trống")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "staff_id", nullable = false)
  private Staff staff;

  @NotNull(message = "Access token không được để trống")
  @Column(name = "access_token", nullable = false)
  private String accessToken;

  @NotNull(message = "Refresh token không được để trống")
  @Column(name = "refresh_token", nullable = false)
  private String refreshToken;

  @Column(name = "token_reset")
  private String tokenReset;

  @NotNull(message = "Thời gian hết hạn không được để trống")
  @Column(name = "expires_at", nullable = false)
  private java.time.LocalDateTime expiresAt;
}
