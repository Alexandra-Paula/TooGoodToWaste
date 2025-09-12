package org.application.waste.service;

import jakarta.validation.Valid;
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
}