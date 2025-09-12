package org.application.waste.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.application.waste.enums.Availability;
import org.application.waste.enums.RecommendationStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_discount_id")
    private Discount productDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_file_id")
    private ProductLink productLink;

    @NotBlank
    @Size(min = 5, max = 80)
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @NotBlank
    @Size(min = 10, max = 80)
    @Column(name = "pickupAddress", nullable = false, length = 80)
    private String pickupAddress;

    @Min(value = 0)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotBlank
    @Size(min = 5, max = 80)
    @Column(name = "quality", nullable = false, length = 80)
    private String quality;

    @NotBlank
    @DecimalMin(value = "0.0")
    @Column(name = "price", nullable = false)
    private double price;

    @NotBlank
    @Size(min = 10)
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @PastOrPresent
    @Column(name = "date_posted", nullable = false)
    private LocalDateTime datePosted;

    @NotBlank
    @Column(name = "rating", nullable = false)
    private int rating;

    @NotBlank
    @Size(min = 5, max = 80)
    @Column(name = "code", nullable = false, length = 80, unique = true)
    private String code;

    @NotBlank
    @Size(min = 4, max = 80)
    @Column(name = "unit", nullable = false, length = 80)
    private String unit;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_status", nullable = false)
    private RecommendationStatus recommendationStatus;

    @NotBlank
    @Size(min = 4, max = 80)
    @Column(name = "product_image", nullable = false)
    private String productImage;

    @NotBlank
    @Size(min = 4, max = 80)
    @Column(name = "company_image", nullable = false)
    private String companyImage;

    @Column(name = "is_deleted")
    private LocalDateTime isDeleted = null;
}