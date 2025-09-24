package org.application.waste.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long id;

    private Long productId;

    private int quantity;

    private int totalQuantity;

    private double subtotal;

    private String name;

    private double finalPrice;

    private String productImage;
}