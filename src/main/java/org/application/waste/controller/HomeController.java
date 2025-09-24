package org.application.waste.controller;

import org.application.waste.dto.ProductDto;
import org.application.waste.dto.ReviewDto;
import org.application.waste.service.CategoryService;
import org.application.waste.service.ProductService;
import org.application.waste.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public HomeController(ProductService productService, CategoryService categoryService, ReviewService reviewService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }
    @GetMapping("/index")
    public String home(Model model) {
        List<ProductDto> products = productService.getAllAvailableProducts();
        List<ProductDto> recommendedProducts = productService.getRecommendedProducts();
        List<ReviewDto> reviews = reviewService.getAllReviews();

        model.addAttribute("popularProducts", recommendedProducts);
        model.addAttribute("recommendedProducts", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("reviews", reviews);

        model.addAttribute("page", "home");
        return "index";
    }
}
