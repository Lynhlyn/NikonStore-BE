package com.example.nikonbe.modules.cart.repository;

import com.example.nikonbe.modules.cart.entity.Cart;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
  Optional<Cart> findByCustomer_Id(Integer customerId);

  @Query(
      "SELECT c FROM Cart c LEFT JOIN FETCH c.cartDetails cd "
          + "LEFT JOIN FETCH cd.productDetail pd "
          + "LEFT JOIN FETCH pd.product p "
          + "LEFT JOIN FETCH pd.color clr "
          + "LEFT JOIN FETCH pd.capacity cap "
          + "WHERE c.customer.id = :customerId")
  Optional<Cart> findCartWithDetailsByCustomerId(@Param("customerId") Integer customerId);

  @Query(
      "SELECT c FROM Cart c LEFT JOIN FETCH c.cartDetails cd "
          + "LEFT JOIN FETCH cd.productDetail pd "
          + "LEFT JOIN FETCH pd.product p "
          + "LEFT JOIN FETCH pd.color clr "
          + "LEFT JOIN FETCH pd.capacity cap "
          + "WHERE c.cookieId = :cookieId")
  Optional<Cart> findCartWithDetailsByCookieId(@Param("cookieId") String cookieId);

  Optional<Cart> findByCookieId(String cookieId);

  void deleteByExpireAtBefore(LocalDateTime expireAt);
}
