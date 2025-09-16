package org.application.waste.service;

import org.application.waste.entity.PasswordResetToken;
import org.application.waste.entity.User;

import java.util.Optional;


public interface PasswordResetTokenService {
    PasswordResetToken findByToken(String token);

    PasswordResetToken save(PasswordResetToken passwordResetToken);

    Optional<PasswordResetToken> findByUser(User user);

    void delete(PasswordResetToken token);
}