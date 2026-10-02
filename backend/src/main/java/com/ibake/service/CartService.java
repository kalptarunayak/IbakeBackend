package com.ibake.service;

import com.ibake.dto.AddToCartRequest;
import com.ibake.dto.CartResponse;
import com.ibake.dto.UpdateCartItemRequest;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addItemToCart(Long userId, AddToCartRequest request);
    CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartResponse removeCartItem(Long userId, Long itemId);
    void clearCart(Long userId);
}
