package com.tfg.terranostra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa un token temporal para restablecer la contraseña de un usuario.
 * Asociado a un email y con una fecha de expiración.
 *
 * @author ebp
 * @version 1.0
 */
@Entity
@Table(name = "password_reset_tokens")
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Email(message = "Debe ser un correo válido.")
    @NotBlank(message = "El email no puede estar vacío.")
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El token no puede estar vacío.")
    private String token;

    @Column(nullable = false)
    @NotNull(message = "La expiración no puede estar vacía.")
    private LocalDateTime expiracion;
}
