package org.application.waste.service;

import org.application.waste.dto.CheckoutDto;
import org.application.waste.entity.Checkout;
import org.application.waste.entity.User;
import org.application.waste.repository.CheckoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;

    public CheckoutServiceImpl(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    @Override
    public Long createOrder(CheckoutDto dto, User user) {
        Checkout checkout = new Checkout();
        checkout.setFirstName(dto.getFirstName());
        checkout.setLastName(dto.getLastName());
        checkout.setEmail(dto.getEmail());
        checkout.setPhoneNumber(dto.getPhoneNumber());
        checkout.setRegion(dto.getRegion());
        checkout.setCity(dto.getCity());
        checkout.setAddress(dto.getAddress());
        checkout.setNotes(dto.getNotes());
        checkout.setPaymentMethod(dto.getPaymentMethod());
        checkout.setTotal(dto.getTotal());
        checkout.setOrderDate(LocalDateTime.now());
        checkout.setUser(user);

        Checkout savedOrder = checkoutRepository.save(checkout);
        return savedOrder.getId();
    }

    @Override
    public CheckoutDto getOrderById(Long orderId) {
        return checkoutRepository.findById(orderId)
                .map(order -> {
                    CheckoutDto dto = new CheckoutDto();
                    dto.setId(order.getId());
                    dto.setTotal(order.getTotal());
                    dto.setOrderDate(order.getOrderDate());
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Comanda nu a fost găsită"));
    }
}