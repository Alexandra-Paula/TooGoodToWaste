package org.application.waste.controller;

import org.application.waste.entity.PasswordReset;
import org.application.waste.entity.PasswordResetToken;
import org.application.waste.entity.User;
import org.application.waste.service.PasswordResetTokenService;
import org.application.waste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordController {
    private final PasswordResetTokenService tokenService;
    private final MessageSource messageSource;
    private final UserService userService;

    @Autowired
    public ResetPasswordController(PasswordResetTokenService tokenService, MessageSource messageSource, UserService userService) {
        this.tokenService = tokenService;
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @GetMapping
    public String viewPage(@RequestParam(name = "token", required = false) String token,
                           Model model) {
        PasswordResetToken passwordResetToken = tokenService.findByToken(token);
        if (passwordResetToken == null) {
            model.addAttribute("error", messageSource.getMessage("TOKEN_NOT_FOUND", new Object[]{}, Locale.ENGLISH));
        } else if (passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", messageSource.getMessage("TOKEN_EXPIRED", new Object[]{}, Locale.ENGLISH));
        } else {
            PasswordReset dto = new PasswordReset();
            dto.setToken(passwordResetToken.getToken()); // setezi token-ul
            model.addAttribute("passwordReset", dto);
        }
        return "reset-password";
    }

    @PostMapping
    public String resetPassword(@ModelAttribute("passwordReset") PasswordReset passwordReset) {

        StringBuilder errors = new StringBuilder();

        if (!passwordReset.getPassword().equals(passwordReset.getConfirmPassword())) {
            errors.append("Parolele nu coincid&");
        }

        if (passwordReset.getPassword().length() < 8) {
            errors.append("Parola+trebuie+sa+aiba+minim+8+caractere&");
        }

        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#\\$%&'0*+,-/:<=>?@^_]).+$";
        if (!passwordReset.getPassword().matches(pattern)) {
            errors.append("Parola+trebuie+sa+contina+cel+putin+o+litera+mare, mica,+o+ cifra+si+un+caracter+special&");
        }
        PasswordResetToken token = tokenService.findByToken(passwordReset.getToken());
        if (token == null) {
            errors.append("Token-ul+nu+a+fost+gasit&");
        } else if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
            errors.append("Token-ul+a+expirat&");
        }

        if (errors.length() > 0) {
            String errorParam = errors.toString();
            if (errorParam.endsWith("&")) errorParam = errorParam.substring(0, errorParam.length() - 1);
            return "redirect:/reset-password?token=" + passwordReset.getToken() + "&error=" + errorParam;
        }

        User user = token.getUser();
        try {
            userService.checkNewPassword(user, passwordReset.getPassword());
            user.setPassword(passwordReset.getPassword());
            userService.updatePassword(user);
        } catch (IllegalArgumentException ex) {
            return "redirect:/reset-password?token=" + passwordReset.getToken() + "&error=" + ex.getMessage().replace(" ", "+");
        }

        return "redirect:/login";
    }

    @ModelAttribute("passwordReset")
    public PasswordReset passwordReset() {
        return new PasswordReset();
    }

}