package org.application.waste.controller;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.application.waste.entity.Mail;
import org.application.waste.entity.PasswordForgot;
import org.application.waste.entity.PasswordResetToken;
import org.application.waste.entity.User;
import org.application.waste.service.EmailService;
import org.application.waste.service.PasswordResetTokenService;
import org.application.waste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    private final UserService userService;
    private final MessageSource messageSource;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    @Autowired
    public ForgotPasswordController(UserService userService, MessageSource messageSource, PasswordResetTokenService passwordResetTokenService, EmailService emailService) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
    }

    @GetMapping
    public String viewPage() {
        return "forgotPassword";
    }

    @PostMapping
    public String processPasswordForgot(@Valid @ModelAttribute("passwordForgot") PasswordForgot passwordForgot,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes attributes,
                                        HttpServletRequest request) {
        String emailInput = passwordForgot.getEmail().trim().toLowerCase();
        System.out.println(">>> Email primit: " + emailInput);

        if (result.hasErrors()) {
            return "forgotPassword";
        }

        Optional<User> optionalUser = userService.findByEmail(emailInput);
        if (optionalUser.isEmpty()) {
            model.addAttribute("error", messageSource.getMessage("EMAIL_NOT_FOUND", null, Locale.getDefault()));
            return "forgotPassword";
        }

        User user = optionalUser.get();

        // Verifică dacă există token pentru user
        Optional<PasswordResetToken> optionalToken = passwordResetTokenService.findByUser(user);

        PasswordResetToken token;

        if (optionalToken.isPresent()) {
            token = optionalToken.get();
            if (token.getExpirationDate().isBefore(LocalDateTime.now())) {
                // Token expirat -> șterge și creează unul nou
                passwordResetTokenService.delete(token);
                token = new PasswordResetToken();
                token.setUser(user);
                token.setToken(UUID.randomUUID().toString());
                token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
                passwordResetTokenService.save(token);
            }
            // dacă nu e expirat, păstrează token-ul existent și nu îl actualiza
        } else {
            // Nu există token -> creează unul nou
            token = new PasswordResetToken();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString());
            token.setExpirationDate(LocalDateTime.now().plusMinutes(30));
            passwordResetTokenService.save(token);
        }

        // Construiește email-ul
        Mail mail = new Mail();
        mail.setFrom("gutanmarina123@gmail.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Password reset request");

        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        Map<String, Object> mailModel = new HashMap<>();
        mailModel.put("user", user);
        mailModel.put("token", token.getToken());
        mailModel.put("resetUrl", url + "/reset-password?token=" + token.getToken());
        mail.setModel(mailModel);

        emailService.send(mail);

        // Flash attribute pentru succes
        attributes.addFlashAttribute("success",
                messageSource.getMessage("PASSWORD_RESET_TOKEN_SENT", null, Locale.getDefault()));

        return "redirect:/forgotPassword";
    }

    @ModelAttribute("passwordForgot")
    public PasswordForgot passwordForgot() {
        return new PasswordForgot();
    }

}


