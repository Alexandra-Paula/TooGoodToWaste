package org.application.waste.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.waste.service.LoginAttemptService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    public CustomLoginFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String errorMessage;

        // Dacă aplicația este blocată global
        if (loginAttemptService.isBlocked()) {
            errorMessage = "Ai depășit numărul de încercări. Încearcă din nou peste 10 minute.";
        } else {
            loginAttemptService.loginFailed();

            if (exception instanceof BadCredentialsException) {
                errorMessage = "Nume de utilizator sau parolă incorectă!";
            } else {
                errorMessage = "A apărut o eroare la autentificare.";
            }
        }

        // Encodăm și trimitem mesajul
        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/login?errorMessage=" + errorMessage);
    }
}
