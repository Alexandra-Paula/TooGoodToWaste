package org.application.waste.controller;

import org.application.waste.dto.ProductDto;
import org.application.waste.dto.ReviewDto;
import org.application.waste.entity.User;
import org.application.waste.service.ProductService;
import org.application.waste.service.ReviewService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, UserService userService, ReviewService reviewService) {
        this.productService = productService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/product-details")
    public String productDetails(@RequestParam("id") Long productId,
                                 @RequestParam(value = "reviewId", required = false) Long reviewId,
                                 Model model, Authentication authentication) {
        ProductDto product = productService.findProductById(productId)
                .orElse(null);

        if (product == null) {
            return "redirect:/shop?error=Produsul nu a fost găsit";
        }

        List<ProductDto> products = productService.getAllAvailableProducts();

        User user = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            user = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        if (user == null || user.getId() == null) {
            return "redirect:/upload?error=Utilizator neautentificat";
        }

        ReviewDto review;
        if (reviewId != null) {
            review = reviewService.getReviewById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("Review cu id-ul " + reviewId + " nu a fost găsit"));

            if (review == null) {
                return "redirect:/shop?error=Recenzia nu a fost găsită";
            }

            review.setCurrentUserName(user.getUsername());
            review.setCurrentUserEmail(user.getEmail());
        } else {
            review = new ReviewDto();
            review.setCurrentUserName(user.getUsername());
            review.setCurrentUserEmail(user.getEmail());
            review.setProductId(product.getId());
        }

        List<ReviewDto> reviews = reviewService.getReviewsByProductId(productId);

        model.addAttribute("reviews", reviews);
        model.addAttribute("review", review);
        model.addAttribute("product", product);
        model.addAttribute("recommendedProducts", products);
        model.addAttribute("page", "product-details");
        return "product-details";
    }
}