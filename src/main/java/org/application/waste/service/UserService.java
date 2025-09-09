package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.dto.UserRegisterDto;
import org.application.waste.entity.User;

import java.util.Optional;

public interface UserService  {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    void saveUser(@Valid UserRegisterDto userRegisterDto);

    boolean usernameExists(String username);

    boolean emailExists(String email);

}
