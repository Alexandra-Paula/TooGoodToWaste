package org.application.waste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountSettingsDto {

    @NotBlank(message = "Prenumele nu poate fi gol sau să depășească 60 de caractere")
    @Size(min = 2, max = 60, message = "Prenumele nu poate fi gol sau sa depaseasca 60 de caractere")
    private String firstName;

    @NotBlank(message = "Numele nu poate fi gol sau să depășeasca 60 de caractere")
    @Size(min = 2, max = 60, message = "Numele nu poate fi gol sau să depășeasca 60 de caractere")
    private String lastName;

    @NotBlank(message = "Emailul nu poate fi gol")
    @Email(message = "Introduceți un email valid")
    private String email;

    @NotBlank(message = "Telefonul nu poate fi gol")
    @Pattern(
            regexp = "^[+]?\\d[\\d\\s().-]{6,}$",
            message = "Număr de telefon invalid. Telefonul trebuie să conțină minim 7 cifre. Sunt permise simboluri uzuale (+, -, spațiu) "
    )
    private String phone;

    private String avatarUrl;
}
