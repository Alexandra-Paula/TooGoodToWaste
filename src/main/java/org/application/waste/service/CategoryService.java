package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.entity.Category;

import java.util.Map;

public interface CategoryService {
    void saveCategory(@Valid Category category);

    Category findByName(String name);

    Map<Category, Integer> getProductCountPerCategory();
}