package org.application.waste.controller;

import org.application.waste.dto.CartItemDto;
import org.application.waste.dto.CheckoutDto;
import org.application.waste.entity.User;
import org.application.waste.service.CartService;
import org.application.waste.service.CheckoutService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CheckoutController {
    private final UserService userService;
    private final CartService cartService;
    private final CheckoutService checkoutService;

    public CheckoutController(UserService userService, CartService cartService, CheckoutService checkoutService) {
        this.userService = userService;
        this.cartService = cartService;
        this.checkoutService = checkoutService;
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
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

        model.addAttribute("checkout", new CheckoutDto());
        model.addAttribute("page", "checkout");
        return "checkout";
    }

    @PostMapping("/checkout/add")
    public String placeOrder(@ModelAttribute("checkout") CheckoutDto checkoutDto,
                             Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            User user = userService.findByEmail(principal)
                    .orElseGet(() -> userService.findByUsername(principal).orElse(null));

            if (user != null) {
                double cartTotal = cartService.getCartTotal(user.getId());

                if (cartTotal <= 0) {
                    return "redirect:/checkout?error=true";
                }

                try {
                    checkoutDto.setTotal(cartTotal);
                    Long orderId = checkoutService.createOrder(checkoutDto, user);
                    cartService.clearCart(user.getId());
                    return "redirect:/order-confirmation?id=" + orderId;
                } catch (Exception e) {
                    return "redirect:/checkout?error=true";
                }
            }
        }

        return "redirect:/checkout?error=true";
    }

    @GetMapping("/order-confirmation")
    public String confirmOrder(@RequestParam("id") Long orderId, Model model) {
        CheckoutDto order = checkoutService.getOrderById(orderId);

        model.addAttribute("order", order);
        model.addAttribute("page", "order-confirmation");
        return "order-confirmation";
    }
}