package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.CorreoVerificacionToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorreoVerificacionTokenRepository extends JpaRepository<CorreoVerificacionToken, Long> {
    Optional<CorreoVerificacionToken> findByToken(String token);
    Optional<CorreoVerificacionToken> findByEmail(String email);
    void deleteByEmail(String email);
}
