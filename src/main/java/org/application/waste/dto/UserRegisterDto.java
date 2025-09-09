package org.application.waste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserRegisterDto {

    @NotBlank
    @Size(min = 5, message = "Username-ul trebuie să aibă cel puțin 5 caractere")
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, message = "Parola trebuie să aibă cel puțin 8 caractere")
    private String password;

    @NotBlank
    private String confirmPassword;

}
