package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.dto.ProductDto;
import org.application.waste.entity.Category;
import org.application.waste.entity.Product;
import org.application.waste.entity.ProductLink;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    void saveProduct(@Valid Product product);

    Optional<Product> findByCode(String code);

    List<Product> findAllByProductLink(ProductLink link);

    void deleteByCodes(Set<String> codes);

    List<ProductDto> getAllProductsDto();

    List<ProductDto> getAllProductsDtoByCategoryId(Long categoryId);

    List<ProductDto> sortProducts(List<ProductDto> products, String sortBy);

    List<ProductDto> filterByPrice(List<ProductDto> products, Double minPrice, Double maxPrice);

    List<ProductDto> filterProductsByRating(List<ProductDto> products, String ratingCategory);

    List<ProductDto> filterProductsByTag(List<ProductDto> products, String tag);

    Optional<ProductDto> findProductById(Long id);

    List<ProductDto> getAllAvailableProducts();

    List<ProductDto> getRecommendedProducts();

    List<Category> getAllCategories();


}