package com.tfg.terranostra.services;

import com.tfg.terranostra.models.PasswordResetToken;
import com.tfg.terranostra.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public boolean validarToken(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> t.getExpiracion().isAfter(LocalDateTime.now()))
                .orElse(false);

    }
}
