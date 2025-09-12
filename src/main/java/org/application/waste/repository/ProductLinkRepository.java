package org.application.waste.repository;

import org.application.waste.entity.ProductLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLinkRepository extends JpaRepository<ProductLink, Long> {
}
