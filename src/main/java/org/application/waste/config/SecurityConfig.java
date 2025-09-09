package org.application.waste.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Listează URL-urile care trebuie permise fără autentificare
        String[] permitAllUrls = {
                "/css/**", "/js/**", "/images/**", "/img/**", "/lib/**",
                "/index", "/register/**", "/login/**"
        };

        http
                .csrf(csrf -> csrf.disable()) // dezactivează CSRF pentru API-uri/stateless
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(permitAllUrls).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/passwords/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // fără sesiuni
                );

        return http.build();
    }
}



