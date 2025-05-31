package com.terra_nostra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para el cambio de contraseña mediante token de recuperación.
 * Incluye el token único y la nueva contraseña que el usuario desea establecer.
 *
 * @author ebp
 * @version 1.0
 */
@Data
public class CambioContraseniaDto {

    @NotBlank(message = "El token no puede estar vacío")
    private String token;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String nuevaContrasenia;
}
