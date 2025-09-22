package org.application.waste.dto;

import lombok.Getter;
import lombok.Setter;
import org.application.waste.entity.Category;
import org.application.waste.entity.Discount;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDto {
    private Long id;

    private String name;

    private String description;

    private double initialPrice;

    private double finalPrice;

    private int rating;

    private String productImage;

    private LocalDateTime datePosted;

    private Discount productDiscount;

    private Category category;

    private String pickupAddress;

    private String quality;

    private String code;

    private String unit;

    private String companyImage;

    private int quantity;
}