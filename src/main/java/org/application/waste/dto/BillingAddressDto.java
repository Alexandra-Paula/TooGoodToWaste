package org.application.waste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BillingAddressDto {

    @NotBlank(message = "Prenumele nu poate fi gol")
    @Size(min = 2, max = 60, message = "Prenumele trebuie să aibă între 2 și 60 de caractere")
    private String billingFirstName;

    @NotBlank(message = "Numele nu poate fi gol")
    @Size(min = 2, max = 60, message = "Numele trebuie să aibă între 2 și 60 de caractere")
    private String billingLastName;

    private String companyName;

    @NotBlank(message = "Adresa nu poate fi goală")
    @Size(min = 5, max = 200, message = "Adresa trebuie să aibă între 5 și 200 de caractere")
    private String streetAddress;

    @NotBlank(message = "Selectează o țară")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Selectează o țară validă")
    private String country;


    @NotBlank(message = "Stat/Județ nu poate fi gol")
    private String state;


    @NotBlank(message = "Emailul nu poate fi gol")
    @Email(message = "Introduceți un email valid")
    private String billingEmail;

    @NotBlank(message = "Telefonul nu poate fi gol")
    @Pattern(
            regexp = "^[+]?\\d[\\d\\s().-]{6,}$",
            message = "Telefonul trebuie să conțină doar cifre și simboluri uzuale (+, -, spațiu)"
    )
    private String billingPhone;
}
