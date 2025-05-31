package com.terra_nostra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

/**
 * DTO (Data Transfer Object) para el proceso de autenticación de usuarios.
 * Contiene los datos necesarios para el inicio de sesión.
 *
 * @author ebp
 * @version 1.0
 */

public class LoginDto {

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasenia;

}