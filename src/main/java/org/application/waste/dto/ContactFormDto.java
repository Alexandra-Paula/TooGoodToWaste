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
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Titlul este obligatoriu")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Mesajul nu poate fi gol")
    @Size(min = 10, max = 5000, message = "Mesajul trebuie să aibă minim 10 caractere")
    private String message;
}
