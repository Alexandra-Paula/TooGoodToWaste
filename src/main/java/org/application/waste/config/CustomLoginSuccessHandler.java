package org.application.waste.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.waste.service.LoginAttemptService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    public CustomLoginSuccessHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Verificăm dacă loginul este blocat
        if (loginAttemptService.isBlocked()) {
            String errorMessage = URLEncoder.encode(
                    "Ai depășit numărul de încercări. Încearcă din nou peste 10 minute.",
                    StandardCharsets.UTF_8);
            response.sendRedirect("/login?errorMessage=" + errorMessage);
            return;
        }

        // Resetăm încercările la login reușit
        loginAttemptService.loginSucceeded();

        response.sendRedirect("/index");
    }
}
