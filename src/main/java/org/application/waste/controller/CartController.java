package org.application.waste.controller;

import org.application.waste.dto.CartItemDto;
import org.application.waste.entity.User;
import org.application.waste.service.CartService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/shopping-cart")
    public String cart(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            User user = userService.findByEmail(principal)
                    .orElseGet(() -> userService.findByUsername(principal).orElse(null));

            if (user != null) {
                List<CartItemDto> cartItems = cartService.getCartItemsByUserId(user.getId());
                model.addAttribute("cartItems", cartItems);

                double cartTotal = cartService.getCartTotal(user.getId());
                model.addAttribute("cartTotal", cartTotal);
            }
        }

        model.addAttribute("page", "cart");
        return "shopping-cart";
    }

    @PostMapping("/cart/remove/{itemId}")
    public String removeItem(@PathVariable Long itemId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = null;
        if (!"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {
            String principal = authentication.getName();
            user = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        try {
            if (user != null) {
                cartService.removeItemFromCart(user.getId(), itemId);
            }
            return "redirect:/shopping-cart?success=true";
        } catch (RuntimeException e) {
            return "redirect:/shopping-cart?error=true";
        }
    }

    @PostMapping("/shopping-cart/add")
    public String addProductToCart(
            @RequestParam("productId") Long productId,
            Authentication authentication) {

        User user = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            user = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        if (user == null) {
            return "redirect:/login?error=true";
        }

        try {
            cartService.addProductToCart(user.getId(), productId);
            return "redirect:/shopping-cart?success=true";
        } catch (RuntimeException e) {
            return "redirect:/shop?error=true";
        }
    }

    @PostMapping("/cart/update")
    public String updateCartItem(
            @RequestParam("itemId") Long itemId,
            @RequestParam("quantity") int quantity,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {
            return "redirect:/login";
        }

        String principal = authentication.getName();
        User user = userService.findByEmail(principal)
                .orElseGet(() -> userService.findByUsername(principal).orElse(null));

        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.updateItemQuantity(user.getId(), itemId, quantity);
            return "redirect:/shopping-cart?success=true";
        } catch (RuntimeException e) {
            return "redirect:/shopping-cart?error=true";
        }
    }
}