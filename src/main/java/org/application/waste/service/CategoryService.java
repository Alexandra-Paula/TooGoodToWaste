package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.entity.Category;

public interface CategoryService {
    void saveCategory(@Valid Category category);

    Category findByName(String name);
}