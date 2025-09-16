package org.application.waste.service;

import org.application.waste.dto.UserRegisterDto;
import org.application.waste.entity.User;
import org.application.waste.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    public void saveUser(UserRegisterDto userRegisterDto) {
        User user = new User();

        user.setUsername(userRegisterDto.getUsername());
        user.setEmail(userRegisterDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username deja folosit");
        }
        userRepository.save(user);
    }
    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    @Override
    @Modifying
    public void updatePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    @Override
    public void checkNewPassword(User user, String newPassword) {
        // Verifică dacă parola nouă coincide cu parola veche
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Parola noua trebuie sa fie diferita de cea anterioara");
        }
    }
}