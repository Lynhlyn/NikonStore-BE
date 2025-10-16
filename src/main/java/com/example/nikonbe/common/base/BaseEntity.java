package com.example.nikonbe.common.base;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

  @CreationTimestamp
  @Column(updatable = false)
  protected LocalDateTime createdAt;

  @UpdateTimestamp protected LocalDateTime updatedAt;
}
