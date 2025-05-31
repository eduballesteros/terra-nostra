package com.tfg.terranostra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para procesar la verificación del correo electrónico mediante token.
 * Se usa en el controlador de verificación para confirmar la cuenta del usuario.
 *
 * @author ebp
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoVerificacionDto {

    @NotBlank(message = "El token no puede estar vacío.")
    private String token;
}
