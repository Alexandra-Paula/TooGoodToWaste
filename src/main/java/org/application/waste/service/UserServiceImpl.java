package org.application.waste.service;

import org.application.waste.dto.UserRegisterDto;
import org.application.waste.entity.User;
import org.application.waste.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegisterDto dto) {


        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return null;
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

}