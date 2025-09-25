package org.application.waste.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordDto {

    @NotBlank(message = "Parola curentă nu poate fi goală")
    private String currentPassword;

    @NotBlank(message = "")
    @Pattern(
            // identic cu regex-ul tău de la înregistrare
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#\\$%&'0*+,-/:<=>?@^_]).{8,}$",
            message = "Parola nouă trebuie să aibă minim 8 caractere și să conțină: literă mare, literă mică, cifră și caracter special (#$%&'0*+,-/:<=>?@^_)"
    )
    private String newPassword;

    @NotBlank(message = "Confirmarea parolei nu poate fi goală")
    private String confirmNewPassword;
}
