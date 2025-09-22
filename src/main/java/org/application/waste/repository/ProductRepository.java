package org.application.waste.repository;

import jakarta.validation.constraints.NotBlank;
import org.application.waste.entity.Product;
import org.application.waste.entity.ProductLink;
import org.application.waste.enums.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);

    List<Product> findAllByProductLink(ProductLink link);

    void deleteByCodeIn(Set<String> codes);

    List<Product> findAll();

    List<Product> findAllByCategoryId(Long categoryId);

    @Query("SELECT p.category.id, p.category.categoryName, COUNT(p) FROM Product p GROUP BY p.category.id, p.category.categoryName")
    List<Object[]> countProductsPerCategory();

    Optional<Product> findById(Long id);

    List<Product> findAllByAvailability(@NotBlank Availability availability);
}