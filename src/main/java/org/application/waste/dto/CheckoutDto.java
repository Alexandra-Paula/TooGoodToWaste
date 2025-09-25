package org.application.waste.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckoutDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String region;

    private String city;

    private String address;

    private String notes;

    private String paymentMethod;

    private Double total;

    private LocalDateTime orderDate;
}
