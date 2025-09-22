package org.application.waste.controller;

import org.application.waste.dto.ProductDto;
import org.application.waste.entity.Category;
import org.application.waste.service.CategoryService;
import org.application.waste.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class ShopController {
    private final CategoryService categoryService;
    private final ProductService productService;

    public ShopController(CategoryService categoryService, ProductService productService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/shop")
    public String shop(Model model,
                       @RequestParam(name = "category", required = false) Long categoryId,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "sortOption", required = false) String sortBy,
                       @RequestParam(name = "minPrice", required = false) Double minPrice,
                       @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                       @RequestParam(name = "ratingCategory", required = false) String ratingCategory,
                       @RequestParam(name = "tag", required = false) String tag) {

        Map<Category, Integer> categoryCounts = categoryService.getProductCountPerCategory();
        List<ProductDto> allProducts = (categoryId != null)
                ? productService.getAllProductsDtoByCategoryId(categoryId)
                : productService.getAllProductsDto();

        if (tag != null) {
            allProducts = productService.filterProductsByTag(allProducts, tag);
        }

        if (ratingCategory != null) {
            allProducts = productService.filterProductsByRating(allProducts, ratingCategory);
        }

        if (sortBy != null) {
            allProducts = productService.sortProducts(allProducts, sortBy);
        }

        if (minPrice != null || maxPrice != null) {
            allProducts = productService.filterByPrice(allProducts, minPrice, maxPrice);
        }

        List<Map.Entry<Category, Integer>> sortedCategoryCounts = categoryCounts.entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().getCategoryName()))
                .toList();

        int pageSize = 12;
        int totalProducts = allProducts.size();
        int totalCountProducts = productService.getAllProductsDto().size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        int pageIndex = page - 1;
        if (pageIndex < 0) pageIndex = 0;

        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, totalProducts);

        List<ProductDto> productsPage = new ArrayList<>();
        if (start < end) {
            productsPage = allProducts.subList(start, end);
        }

        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("products", productsPage);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categoryCounts", sortedCategoryCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCountProducts", totalCountProducts);
        model.addAttribute("selectedSort", sortBy);
        model.addAttribute("tag", tag);
        model.addAttribute("ratingCategory", ratingCategory);
        model.addAttribute("page", "shop");

        return "shop";
    }
}