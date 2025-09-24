package org.application.waste.service;

import org.application.waste.entity.Category;
import org.application.waste.repository.CategoryRepository;
import org.application.waste.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByCategoryName(name).orElse(null);
    }

    @Override
    public Map<Category, Integer> getProductCountPerCategory() {
        List<Object[]> results = productRepository.countProductsPerCategory();
        Map<Category, Integer> categoryCountMap = new HashMap<>();

        for (Object[] row : results) {
            Long categoryId = (Long) row[0];
            String categoryName = (String) row[1];
            Long count = (Long) row[2];

            Category category = new Category();
            category.setId(categoryId);
            category.setCategoryName(categoryName);

            categoryCountMap.put(category, count.intValue());
        }

        return categoryCountMap;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(); //3
    }


}