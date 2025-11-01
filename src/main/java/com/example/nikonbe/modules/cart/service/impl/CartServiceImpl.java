package com.example.nikonbe.modules.cart.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.cart.dto.request.AddToCartRequest;
import com.example.nikonbe.modules.cart.dto.request.DeleteCartItemRequest;
import com.example.nikonbe.modules.cart.dto.request.GetCartRequest;
import com.example.nikonbe.modules.cart.dto.request.UpdateCartItemRequest;
import com.example.nikonbe.modules.cart.dto.response.CartResponse;
import com.example.nikonbe.modules.cart.entity.Cart;
import com.example.nikonbe.modules.cart.mapper.CartMapper;
import com.example.nikonbe.modules.cart.repository.CartRepository;
import com.example.nikonbe.modules.cart.service.interF.CartService;
import com.example.nikonbe.modules.cart_detail.dto.response.CartItemResponse;
import com.example.nikonbe.modules.cart_detail.entity.CartDetail;
import com.example.nikonbe.modules.cart_detail.mapper.CartDetailMapper;
import com.example.nikonbe.modules.cart_detail.repository.CartDetailRepository;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.product_detail.repository.ProductDetailRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final CustomerRepository customerRepository;
  private final CartDetailMapper cartDetailMapper;
  private final CartMapper cartMapper;
  private final ProductDetailRepository productDetailRepository;
  private final CartDetailRepository cartDetailRepository;

  private static final long COOKIE_EXPIRY_DAYS = 5;

  private void validateCustomer(Integer customerId) {
    if (customerId != null && !customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer", "id", customerId);
    }
  }

  private ProductDetail validateProduct(Integer productId, Integer quantity) {
    ProductDetail productDetail =
        productDetailRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductDetail", "id", productId));

    if (quantity != null
        && productDetail.getStock() != null
        && productDetail.getStock() < quantity) {
      Map<String, String> errors = new HashMap<>();
      errors.put("quantity", "Số lượng yêu cầu vượt quá tồn kho");
      throw new ValidationException("Không đủ hàng trong kho", errors);
    }
    return productDetail;
  }

  public Cart getOrCreateCart(GetCartRequest request) {
    if (request.getCustomerId() != null) {
      validateCustomer(request.getCustomerId());
      return cartRepository
          .findCartWithDetailsByCustomerId(request.getCustomerId())
          .orElseGet(
              () -> {
                Cart newCart = new Cart();
                Customer customer =
                    customerRepository
                        .findById(request.getCustomerId())
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    "Customer", "id", request.getCustomerId()));
                newCart.setCustomer(customer);
                newCart.setCartDetails(new ArrayList<>());
                return cartRepository.save(newCart);
              });
    } else if (request.getCookieId() != null) {
      Optional<Cart> cart = cartRepository.findCartWithDetailsByCookieId(request.getCookieId());
      if (cart.isEmpty()) {
        Cart newCart = new Cart();
        newCart.setCookieId(request.getCookieId());
        newCart.setCartDetails(new ArrayList<>());
        newCart.setExpireAt(LocalDateTime.now().plusDays(COOKIE_EXPIRY_DAYS));
        return cartRepository.save(newCart);
      } else {
        return cart.get();
      }
    } else {
      Map<String, String> errors = new HashMap<>();
      errors.put("request", "Vui lòng cung cấp mã khách hàng hoặc cookie ID");
      throw new ValidationException("Thiếu thông tin xác định giỏ hàng", errors);
    }
  }

  private void refreshCartExpiry(Cart cart) {
    if (cart.getCookieId() != null) {
      cart.setExpireAt(LocalDateTime.now().plusDays(COOKIE_EXPIRY_DAYS));
      cartRepository.save(cart);
    }
  }

  @Override
  public CartResponse getCart(GetCartRequest request) {
    Cart cart = getOrCreateCart(request);
    refreshCartExpiry(cart);

    CartResponse cartResponse = cartMapper.toCartResponse(cart);
    List<CartItemResponse> items =
        cart.getCartDetails().stream()
            .map(
                cartDetail -> {
                  CartItemResponse itemResponse = cartDetailMapper.toCartItemResponse(cartDetail);
                  itemResponse.setDiscount(BigDecimal.ZERO);
                  BigDecimal unitPrice = cartDetail.getPrice();
                  itemResponse.setTotalPrice(
                      unitPrice.multiply(BigDecimal.valueOf(cartDetail.getQuantity())));
                  return itemResponse;
                })
            .collect(Collectors.toList());
    cartResponse.setItems(items);

    if (items.isEmpty()) {
      cartResponse.setCartId(null);
      cartResponse.setCustomerId(request.getCustomerId());
      cartResponse.setCookieId(request.getCookieId());
      cartResponse.setItems(new ArrayList<>());
    }

    return cartResponse;
  }

  @Override
  public CartResponse addToCart(AddToCartRequest request) {
    if ((request.getCustomerId() == null && request.getCookieId() == null)
        || request.getProductId() == null
        || request.getQuantity() == null) {
      Map<String, String> errors = new HashMap<>();
      if (request.getCustomerId() == null && request.getCookieId() == null)
        errors.put("request", "Vui lòng cung cấp mã khách hàng hoặc cookie ID");
      if (request.getProductId() == null) errors.put("productId", "Mã sản phẩm là bắt buộc");
      if (request.getQuantity() == null) errors.put("quantity", "Số lượng là bắt buộc");
      throw new ValidationException("Yêu cầu không hợp lệ", errors);
    }

    if (request.getQuantity() <= 0) {
      Map<String, String> errors = new HashMap<>();
      errors.put("quantity", "Số lượng phải lớn hơn 0");
      throw new ValidationException("Số lượng không hợp lệ", errors);
    }

    validateCustomer(request.getCustomerId());
    validateProduct(request.getProductId(), request.getQuantity());

    Cart cart = getOrCreateCart(new GetCartRequest(request.getCustomerId(), request.getCookieId()));
    refreshCartExpiry(cart);

    Optional<CartDetail> existingCartDetail =
        cart.getCartDetails().stream()
            .filter(detail -> detail.getProductDetail().getId().equals(request.getProductId()))
            .findFirst();

    if (existingCartDetail.isPresent()) {
      CartDetail cartDetail = existingCartDetail.get();
      int newQuantity = cartDetail.getQuantity() + request.getQuantity();
      if (newQuantity <= 0) {
        Map<String, String> errors = new HashMap<>();
        errors.put("quantity", "Số lượng sau khi cộng không được âm hoặc bằng 0");
        throw new ValidationException("Số lượng không hợp lệ", errors);
      }
      cartDetail.setQuantity(newQuantity);
      ProductDetail pdRef = productDetailRepository.getReferenceById(request.getProductId());
      cartDetail.setPrice(pdRef.getPrice());
      cartDetailRepository.save(cartDetail);
    } else {
      CartDetail cartDetail = new CartDetail();
      cartDetail.setCart(cart);
      ProductDetail pdRef = productDetailRepository.getReferenceById(request.getProductId());
      cartDetail.setProductDetail(pdRef);
      cartDetail.setQuantity(request.getQuantity());

      cartDetail.setPrice(pdRef.getPrice());
      cart.getCartDetails().add(cartDetail);
      cartDetailRepository.save(cartDetail);
    }

    cart = cartRepository.save(cart);

    List<CartItemResponse> items =
        cart.getCartDetails().stream()
            .map(
                cartDetail -> {
                  CartItemResponse itemResponse = cartDetailMapper.toCartItemResponse(cartDetail);
                  itemResponse.setDiscount(BigDecimal.ZERO);
                  BigDecimal unitPrice = cartDetail.getPrice();
                  itemResponse.setTotalPrice(
                      unitPrice.multiply(BigDecimal.valueOf(cartDetail.getQuantity())));
                  return itemResponse;
                })
            .collect(Collectors.toList());
    CartResponse cartResponse = cartMapper.toCartResponse(cart);
    cartResponse.setItems(items);
    cartResponse.setCookieId(request.getCookieId());

    return cartResponse;
  }

  @Override
  public CartResponse updateCartItem(UpdateCartItemRequest request) {
    if ((request.getCustomerId() == null && request.getCookieId() == null)
        || request.getProductId() == null
        || request.getQuantity() == null) {
      Map<String, String> errors = new HashMap<>();
      if (request.getCustomerId() == null && request.getCookieId() == null)
        errors.put("request", "Vui lòng cung cấp mã khách hàng hoặc cookie ID");
      if (request.getProductId() == null) errors.put("productId", "Mã sản phẩm là bắt buộc");
      if (request.getQuantity() == null) errors.put("quantity", "Số lượng là bắt buộc");
      throw new ValidationException("Yêu cầu không hợp lệ", errors);
    }

    if (request.getQuantity() <= 0) {
      Map<String, String> errors = new HashMap<>();
      errors.put("quantity", "Số lượng phải lớn hơn 0");
      throw new ValidationException("Số lượng không hợp lệ", errors);
    }

    validateCustomer(request.getCustomerId());
    validateProduct(request.getProductId(), request.getQuantity());
    ProductDetail prd = productDetailRepository.getReferenceById(request.getProductId());
    Cart cart = getOrCreateCart(new GetCartRequest(request.getCustomerId(), request.getCookieId()));
    refreshCartExpiry(cart);

    CartDetail cartDetail =
        cart.getCartDetails().stream()
            .filter(detail -> detail.getProductDetail().getId().equals(request.getProductId()))
            .findFirst()
            .orElseThrow(
                () -> new ResourceNotFoundException("Sản phẩm không tồn tại trong giỏ hàng"));

    cartDetail.setQuantity(request.getQuantity());
    cartDetail.setPrice(prd.getPrice());
    cartDetailRepository.save(cartDetail);

    cart = cartRepository.save(cart);

    List<CartItemResponse> items =
        cart.getCartDetails().stream()
            .map(
                detail -> {
                  CartItemResponse itemResponse = cartDetailMapper.toCartItemResponse(detail);
                  itemResponse.setDiscount(BigDecimal.ZERO);
                  BigDecimal unitPrice = detail.getPrice();
                  itemResponse.setTotalPrice(
                      unitPrice.multiply(BigDecimal.valueOf(detail.getQuantity())));
                  return itemResponse;
                })
            .collect(Collectors.toList());
    CartResponse cartResponse = cartMapper.toCartResponse(cart);
    cartResponse.setItems(items);
    cartResponse.setCookieId(request.getCookieId());

    return cartResponse;
  }

  @Override
  public CartResponse deleteCartItem(DeleteCartItemRequest request) {
    if ((request.getCustomerId() == null && request.getCookieId() == null)
        || request.getProductId() == null) {
      Map<String, String> errors = new HashMap<>();
      if (request.getCustomerId() == null && request.getCookieId() == null)
        errors.put("request", "Vui lòng cung cấp mã khách hàng hoặc cookie ID");
      if (request.getProductId() == null) errors.put("productId", "Mã sản phẩm là bắt buộc");
      throw new ValidationException("Yêu cầu không hợp lệ", errors);
    }

    validateCustomer(request.getCustomerId());
    validateProduct(request.getProductId(), 0);

    Cart cart = getOrCreateCart(new GetCartRequest(request.getCustomerId(), request.getCookieId()));
    refreshCartExpiry(cart);

    CartDetail cartDetail =
        cart.getCartDetails().stream()
            .filter(detail -> detail.getProductDetail().getId().equals(request.getProductId()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "CartDetail", "productId", request.getProductId()));

    cart.getCartDetails().remove(cartDetail);
    cartDetailRepository.delete(cartDetail);

    cart = cartRepository.save(cart);

    List<CartItemResponse> items =
        cart.getCartDetails().stream()
            .map(
                cartDetail1 -> {
                  CartItemResponse itemResponse = cartDetailMapper.toCartItemResponse(cartDetail1);
                  itemResponse.setDiscount(BigDecimal.ZERO);
                  BigDecimal unitPrice = cartDetail1.getPrice();
                  itemResponse.setTotalPrice(
                      unitPrice.multiply(BigDecimal.valueOf(cartDetail1.getQuantity())));
                  return itemResponse;
                })
            .collect(Collectors.toList());
    CartResponse cartResponse = cartMapper.toCartResponse(cart);
    cartResponse.setItems(items);
    cartResponse.setCookieId(request.getCookieId());

    return cartResponse;
  }

  public CartResponse assignCartToCustomer(Integer customerId, String cookieId) {
    validateCustomer(customerId);
    if (cookieId == null) {
      throw new ValidationException("Cookie ID là bắt buộc", new HashMap<>());
    }

    Cart cookieCart =
        cartRepository
            .findCartWithDetailsByCookieId(cookieId)
            .orElseGet(
                () -> {
                  Cart newCart = new Cart();
                  newCart.setCookieId(cookieId);
                  newCart.setCartDetails(new ArrayList<>());
                  newCart.setExpireAt(LocalDateTime.now().plusDays(COOKIE_EXPIRY_DAYS));
                  return cartRepository.save(newCart);
                });

    Optional<Cart> existingCustomerCart =
        cartRepository.findCartWithDetailsByCustomerId(customerId);
    Cart customerCart;

    if (existingCustomerCart.isPresent()) {
      customerCart = existingCustomerCart.get();
      mergeCarts(customerCart, cookieCart);
    } else {
      customerCart = cookieCart;
      Customer customer =
          customerRepository
              .findById(customerId)
              .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
      customerCart.setCustomer(customer);
      customerCart.setCookieId(null);
      customerCart.setExpireAt(null);
    }

    customerCart = cartRepository.save(customerCart);
    if (cookieCart != customerCart) {
      cartRepository.delete(cookieCart);
    }

    List<CartItemResponse> items =
        customerCart.getCartDetails().stream()
            .map(
                cartDetail -> {
                  CartItemResponse itemResponse = cartDetailMapper.toCartItemResponse(cartDetail);
                  itemResponse.setDiscount(BigDecimal.ZERO);
                  BigDecimal unitPrice = cartDetail.getPrice();
                  itemResponse.setTotalPrice(
                      unitPrice.multiply(BigDecimal.valueOf(cartDetail.getQuantity())));
                  return itemResponse;
                })
            .collect(Collectors.toList());
    CartResponse cartResponse = cartMapper.toCartResponse(customerCart);
    cartResponse.setItems(items);
    cartResponse.setCookieId(null);

    return cartResponse;
  }

  private void mergeCarts(Cart customerCart, Cart cookieCart) {
    Map<Integer, CartDetail> customerItems = new HashMap<>();
    for (CartDetail detail : customerCart.getCartDetails()) {
      customerItems.put(detail.getProductDetail().getId(), detail);
    }

    for (CartDetail cookieDetail : cookieCart.getCartDetails()) {
      Integer productId = cookieDetail.getProductDetail().getId();
      if (customerItems.containsKey(productId)) {
        CartDetail customerDetail = customerItems.get(productId);
        int newQuantity = customerDetail.getQuantity() + cookieDetail.getQuantity();
        if (newQuantity <= 0) {
          Map<String, String> errors = new HashMap<>();
          errors.put("quantity", "Số lượng sau khi cộng không được âm hoặc bằng 0");
          throw new ValidationException("Số lượng không hợp lệ", errors);
        }
        customerDetail.setQuantity(newQuantity);
        customerDetail.setPrice(getProductPrice(productId));
        cartDetailRepository.save(customerDetail);
      } else {
        CartDetail newDetail = new CartDetail();
        newDetail.setCart(customerCart);
        newDetail.setProductDetail(cookieDetail.getProductDetail());
        newDetail.setQuantity(cookieDetail.getQuantity());
        newDetail.setPrice(getProductPrice(productId));
        customerCart.getCartDetails().add(newDetail);
        cartDetailRepository.save(newDetail);
      }
    }
  }

  private BigDecimal getProductPrice(Integer productId) {
    ProductDetail productDetail =
        productDetailRepository
            .findById(productId)
            .orElseThrow(() -> new ValidationException("Sản phẩm không tồn tại", new HashMap<>()));

    return productDetail.getPrice();
  }

  @Transactional
  public void deleteExpiredCarts() {
    cartRepository.deleteByExpireAtBefore(LocalDateTime.now());
  }
}
