package org.application.waste.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.waste.entity.User;
import org.application.waste.repository.UserRepository;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;

    public CustomLoginFailureHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");

        userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .ifPresent(user -> {
                    int newAttempts = user.getFailedAttempts() + 1;
                    user.setFailedAttempts(newAttempts);

                    if (newAttempts >= 2) {
                        user.setLockedUntil(LocalDateTime.now().plusMinutes(10));
                    }

                    userRepository.save(user);
                });

        String errorMessage;
        if (exception.getMessage().contains("blocked") || exception.getMessage().contains("Contul este blocat")) {
            errorMessage = "Număr depășit de încercări. Încercați din nou peste 10 minute.";
        } else {
            errorMessage = "Username sau parolă incorecte!";
        }

        response.sendRedirect("/login?errorMessage=" +
                URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}
