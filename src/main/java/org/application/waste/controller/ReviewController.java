package org.application.waste.controller;

import org.application.waste.dto.ReviewDto;
import org.application.waste.entity.User;
import org.application.waste.service.ReviewService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/submit-review")
    public String saveReview(@ModelAttribute("review") ReviewDto reviewDto,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
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

            if (reviewDto.getId() != null) {
                reviewService.updateReview(reviewDto);
            } else {
                reviewService.saveReview(reviewDto, user);
            }

            reviewService.updateProductRating(reviewDto.getProductId());

            redirectAttributes.addAttribute("success", true);
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/product-details?id=" + reviewDto.getProductId();
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id,
                               @RequestParam("productId") Long productId,
                               RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReviewById(id);
            reviewService.updateProductRating(productId);
            redirectAttributes.addAttribute("success", true);
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        return "redirect:/product-details?id=" + productId;
    }
}