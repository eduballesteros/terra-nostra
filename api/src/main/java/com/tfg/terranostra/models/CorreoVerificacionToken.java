package com.tfg.terranostra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * Entidad que representa un token temporal para verificar el correo de un usuario.
 * Asociado a un email y con una fecha de expiración.
 * Se elimina una vez confirmado.
 *
 * @author ebp
 * @version 1.0
 */
@Entity
@Table(name = "correo_verificacion_tokens")
@Data
public class CorreoVerificacionToken {

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
