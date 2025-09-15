package org.application.waste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#\\$%&'0*+,-/:<=>?@^_]).{8,}$",
            message = "Parola trebuie să aibă minim 8 caractere și să conțină cel puțin: o literă mare, o literă mică, o cifră și un caracter special (#$%&'0*+,-/:<=>?@^_)"
    )
    private String password;

    @NotBlank
    private String confirmPassword;
}
