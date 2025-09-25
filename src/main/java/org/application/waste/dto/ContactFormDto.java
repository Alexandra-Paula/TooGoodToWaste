package org.application.waste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContactFormDto {

    @NotBlank(message = "Numele este obligatoriu")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Emailul este obligatoriu")
    @Email(message = "Email invalid")
    @Email(message = "Emailul trebuie să fie de forma nume@domeniu.tld")
    @Size(max = 150)
    @jakarta.validation.constraints.Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Emailul trebuie să fie de forma nume@domeniu.tld"
    )
    private String email;

    @NotBlank(message = "Titlul este obligatoriu")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Mesajul nu poate fi gol")
    @Size(min = 1, max = 5000, message = "Mesajul trebuie sa fie gol sau mai mare de 5000 de caractere")
    private String message;
}