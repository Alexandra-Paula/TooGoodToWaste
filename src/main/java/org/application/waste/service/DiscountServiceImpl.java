package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.entity.Discount;
import org.application.waste.repository.DiscountRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public void saveDiscount(@Valid Discount discount) {
        discountRepository.save(discount);
    }
}