package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.dto.UserRegisterDto;
import org.application.waste.entity.User;

public interface UserService  {

    User findByEmail(String email);

    User findByUsername(String username);  // Adăugăm această metodă

    void saveUser(@Valid UserRegisterDto userRegisterDto);

    boolean usernameExists(String username);

    boolean emailExists(String email);

}
