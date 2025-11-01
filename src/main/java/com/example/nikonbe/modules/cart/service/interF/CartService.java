package com.example.nikonbe.modules.cart.service.interF;

import com.example.nikonbe.modules.cart.dto.request.AddToCartRequest;
import com.example.nikonbe.modules.cart.dto.request.DeleteCartItemRequest;
import com.example.nikonbe.modules.cart.dto.request.GetCartRequest;
import com.example.nikonbe.modules.cart.dto.request.UpdateCartItemRequest;
import com.example.nikonbe.modules.cart.dto.response.CartResponse;

public interface CartService {

  CartResponse getCart(GetCartRequest request);

  CartResponse addToCart(AddToCartRequest request);

  CartResponse updateCartItem(UpdateCartItemRequest request);

  CartResponse deleteCartItem(DeleteCartItemRequest request);

  CartResponse assignCartToCustomer(Integer customerId, String cookieId);
}
