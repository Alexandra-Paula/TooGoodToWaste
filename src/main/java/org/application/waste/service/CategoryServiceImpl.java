package org.application.waste.service;

import org.application.waste.entity.Category;
import org.application.waste.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
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
}