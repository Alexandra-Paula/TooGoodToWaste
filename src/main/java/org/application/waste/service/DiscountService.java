package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.entity.Discount;

public interface DiscountService {
    void saveDiscount(@Valid Discount discount);
}