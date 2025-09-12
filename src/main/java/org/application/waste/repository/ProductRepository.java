package org.application.waste.repository;

import org.application.waste.entity.Product;
import org.application.waste.entity.ProductLink;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCode(String code);
    Optional<Product> findByCode(String code);

    @Override
    List<Product> findAll(Sort sort);

    List<Product> findAllByProductLink(ProductLink link);

    void deleteByCodeIn(Set<String> codes);

}
