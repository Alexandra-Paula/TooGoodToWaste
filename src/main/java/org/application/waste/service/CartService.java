package org.application.waste.service;

import org.application.waste.dto.CartItemDto;

import java.util.List;

public interface CartService {
    void addProductToCart(Long userId, Long productId);

    List<CartItemDto> getCartItemsByUserId(Long userId);

    void removeItemFromCart(Long userId, Long itemId);

    void updateItemQuantity(Long userId, Long itemId, int newQuantity);

    double getCartTotal(Long userId);

    void clearCart(Long userId);
}