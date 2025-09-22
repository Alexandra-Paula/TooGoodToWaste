package org.application.waste.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.waste.entity.User;
import org.application.waste.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomLoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String username = authentication.getName();

        userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .ifPresent(user -> {
                    user.setFailedAttempts(0);
                    user.setLockedUntil(null);
                    userRepository.save(user);
                });

        response.sendRedirect("/index");
    }
}
