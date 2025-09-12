package org.application.waste.service;

import org.application.waste.entity.Product;
import org.application.waste.entity.ProductLink;
import org.application.waste.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return productRepository.findByCode(code);
    }

    @Override
    public List<Product> findAllByProductLink(ProductLink link) {
        return productRepository.findAllByProductLink(link);
    }

    @Override
    public void deleteByCodes(Set<String> codes) {
        productRepository.deleteByCodeIn(codes);
    }
}