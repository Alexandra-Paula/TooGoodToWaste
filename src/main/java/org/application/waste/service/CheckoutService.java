package org.application.waste.service;

import org.application.waste.dto.CheckoutDto;
import org.application.waste.entity.User;

public interface CheckoutService {
    Long createOrder(CheckoutDto checkoutDto, User user);

    CheckoutDto getOrderById(Long orderId);
}