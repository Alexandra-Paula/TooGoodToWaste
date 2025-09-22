package org.application.waste.service;

import org.application.waste.dto.ProductDto;
import org.application.waste.entity.Product;
import org.application.waste.entity.ProductLink;
import org.application.waste.enums.Availability;
import org.application.waste.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public List<ProductDto> getAllProductsDto() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();

        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setInitialPrice(product.getInitialPrice());
            productDto.setFinalPrice(product.getFinalPrice());
            productDto.setRating(product.getRating());
            productDto.setProductImage(product.getProductImage());
            productDto.setDatePosted(product.getDatePosted());
            productDto.setProductDiscount(product.getProductDiscount());

            productDtos.add(productDto);
        }

        return productDtos;
    }

    @Override
    public List<ProductDto> getAllProductsDtoByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategoryId(categoryId);
        List<ProductDto> productDtos = new ArrayList<>();

        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setInitialPrice(product.getInitialPrice());
            productDto.setFinalPrice(product.getFinalPrice());
            productDto.setRating(product.getRating());
            productDto.setProductImage(product.getProductImage());
            productDto.setDatePosted(product.getDatePosted());
            productDto.setProductDiscount(product.getProductDiscount());

            productDtos.add(productDto);
        }

        return productDtos;
    }

    @Override
    public List<ProductDto> sortProducts(List<ProductDto> products, String sortBy) {
        if (products == null) return List.of();

        Comparator<ProductDto> comparator;

        switch (sortBy) {
            case "recente":
                comparator = Comparator.comparing(ProductDto::getDatePosted).reversed();
                break;
            case "vechi":
                comparator = Comparator.comparing(ProductDto::getDatePosted);
                break;
            case "expirare":
                comparator = Comparator
                        .comparing((ProductDto p) -> p.getProductDiscount().getExpiryDate())
                        .thenComparing(ProductDto::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comparator = (p1, p2) -> 0;
        }

        return products.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> filterByPrice(List<ProductDto> products, Double minPrice, Double maxPrice) {
        return products.stream()
                .filter(p -> (minPrice == null || p.getFinalPrice() >= minPrice) && (maxPrice == null || p.getFinalPrice() <= maxPrice))
                .toList();
    }

    @Override
    public List<ProductDto> filterProductsByRating(List<ProductDto> products, String ratingCategory) {
        double minRating;

        switch (ratingCategory) {
            case "5":
                minRating = 5.0;
                break;
            case "4":
                minRating = 4.0;
                break;
            case "3":
                minRating = 3.0;
                break;
            case "2":
                minRating = 2.0;
                break;
            case "1":
                minRating = 1.0;
                break;
            default:
                minRating = 0.0;
        }

        return products.stream()
                .filter(p -> p.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> filterProductsByTag(List<ProductDto> products, String tag) {
        if (tag == null || tag.isEmpty()) {
            return products;
        }

        String lowerTag = tag.toLowerCase();
        return products.stream()
                .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(lowerTag)) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerTag)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDto> findProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    ProductDto productDto = new ProductDto();
                    productDto.setId(product.getId());
                    productDto.setName(product.getName());
                    productDto.setInitialPrice(product.getInitialPrice());
                    productDto.setFinalPrice(product.getFinalPrice());
                    productDto.setRating(product.getRating());
                    productDto.setProductImage(product.getProductImage());
                    productDto.setProductDiscount(product.getProductDiscount());
                    productDto.setDescription(product.getDescription());
                    productDto.setCode(product.getCode());
                    productDto.setCategory(product.getCategory());
                    productDto.setQuantity(product.getQuantity());
                    productDto.setQuality(product.getQuality());
                    productDto.setCompanyImage(product.getCompanyImage());
                    productDto.setPickupAddress(product.getPickupAddress());
                    return productDto;
                });
    }

    @Override
    public List<ProductDto> getAllAvailableProducts() {
        List<Product> products = productRepository.findAllByAvailability(Availability.DISPONIBIL);
        List<ProductDto> productDtos = new ArrayList<>();

        for (Product product : products) {
            ProductDto dto = new ProductDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setInitialPrice(product.getInitialPrice());
            dto.setFinalPrice(product.getFinalPrice());
            dto.setRating(product.getRating());
            dto.setProductImage(product.getProductImage());
            dto.setProductDiscount(product.getProductDiscount());
            dto.setDescription(product.getDescription());
            dto.setCode(product.getCode());
            dto.setCategory(product.getCategory());
            dto.setQuantity(product.getQuantity());
            dto.setQuality(product.getQuality());
            dto.setCompanyImage(product.getCompanyImage());
            dto.setPickupAddress(product.getPickupAddress());

            productDtos.add(dto);
        }

        return productDtos;
    }
}