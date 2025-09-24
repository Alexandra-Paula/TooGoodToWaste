package org.application.waste.service;

import org.application.waste.dto.CartItemDto;
import org.application.waste.entity.Cart;
import org.application.waste.entity.CartItem;
import org.application.waste.entity.Product;
import org.application.waste.entity.User;
import org.application.waste.repository.CartItemRepository;
import org.application.waste.repository.CartRepository;
import org.application.waste.repository.ProductRepository;
import org.application.waste.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void addProductToCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    newItem.setSubtotal(0.0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + 1);
        item.setSubtotal(item.getQuantity() * product.getFinalPrice());
        item.setAddedDate(LocalDateTime.now());

        cart.setLastUpdated(LocalDateTime.now());

        cartRepository.save(cart);
    }

    @Override
    public List<CartItemDto> getCartItemsByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getItems().stream().map(item -> {
                    CartItemDto dto = new CartItemDto();
                    dto.setId(item.getId());
                    dto.setQuantity(item.getQuantity());
                    dto.setSubtotal(Math.round(item.getSubtotal() * 10.0) / 10.0);
                    dto.setName(item.getProduct().getName());
                    dto.setFinalPrice(item.getProduct().getFinalPrice());
                    dto.setProductImage(item.getProduct().getProductImage());
                    dto.setProductId(item.getProduct().getId());
                    dto.setTotalQuantity(item.getProduct().getQuantity());
                    return dto;
                }).collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @Override
    public void removeItemFromCart(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Coșul nu există pentru acest utilizator"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item-ul nu există în coș"));

        cart.getItems().remove(item);

        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long userId, Long itemId, int newQuantity) {
        CartItem item = cartItemRepository.findByIdAndCartUserId(itemId, userId)
                .orElseThrow(() -> new RuntimeException("Item not found for this user"));

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (newQuantity > item.getProduct().getQuantity()) {
            throw new IllegalArgumentException("Quantity exceeds available stock");
        }

        item.setQuantity(newQuantity);
        item.setSubtotal(item.getQuantity() * item.getProduct().getFinalPrice());
        cartItemRepository.save(item);
    }

    @Override
    public double getCartTotal(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    double total = cart.getItems().stream()
                            .mapToDouble(CartItem::getSubtotal)
                            .sum();
                    return Math.round(total * 10.0) / 10.0;
                })
                .orElse(0.0);
    }
}