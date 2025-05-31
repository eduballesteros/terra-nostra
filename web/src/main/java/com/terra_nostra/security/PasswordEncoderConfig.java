package com.terra_nostra.security;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

@Configuration

/**
 * Configuración de seguridad para la codificación de contraseñas.
 * Define un `PasswordEncoder` basado en `BCrypt` para garantizar
 * la seguridad de las contraseñas en la aplicación.
 *
 * @author ebp
 * @version 1.0
 */


public class PasswordEncoderConfig {

    /**
     * Crea y proporciona un `PasswordEncoder` basado en `BCryptPasswordEncoder`.
     * `BCrypt` es un algoritmo de hash seguro que incluye un mecanismo de salting para proteger contraseñas.
     * @return Instancia de `PasswordEncoder` basada en `BCryptPasswordEncoder`.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
