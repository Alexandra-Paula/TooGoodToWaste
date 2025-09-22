package org.application.waste.service;

import org.application.waste.entity.PasswordResetToken;
import org.application.waste.entity.User;
import org.application.waste.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    @Autowired
    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public PasswordResetToken findByToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        return tokenRepository.save(passwordResetToken);
    }

    @Override
    public Optional<PasswordResetToken> findByUser(User user) {
        return tokenRepository.findByUser(user);
    }

    @Override
    public void delete(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}