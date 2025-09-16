package org.application.waste.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.application.waste.service.PasswordConfirmation;

@Data
@PasswordConfirmation(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "PAROLELE NU COINCID"
)
public class PasswordReset {
    @NotBlank
    @Size(min = 8, message = "Parola trebuie să aibă cel puțin 8 caractere")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#\\$%&'0*+,-/:<=>?@^_]).{8,}$",
            message = "Parola trebuie să conțină cel puțin: o literă mare, o literă mică, o cifră și un caracter special (#$%&'0*+,-/:<=>?@^_)"
    )
    private String password;
    @NotBlank(message = "{PASSWORDS_NOT_EQUAL}")
    private String confirmPassword;
    @NotEmpty
    private String token;
}