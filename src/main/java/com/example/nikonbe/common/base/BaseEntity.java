package com.example.nikonbe.common.base;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

  @CreationTimestamp
  @Column(updatable = false)
  protected LocalDateTime createdAt;

  @UpdateTimestamp protected LocalDateTime updatedAt;
}
