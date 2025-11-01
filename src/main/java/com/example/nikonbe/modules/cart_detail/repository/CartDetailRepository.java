package com.example.nikonbe.modules.cart_detail.repository;

import com.example.nikonbe.modules.cart_detail.entity.CartDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
  List<CartDetail> findByCartId(Integer cartId);
}
