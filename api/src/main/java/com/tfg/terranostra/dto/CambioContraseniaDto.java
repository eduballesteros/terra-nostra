package com.tfg.terranostra.dto;

import lombok.Data;

/**
 * DTO utilizado para representar los datos necesarios
 * al realizar un cambio de contraseña mediante token.
 */

@Data
public class CambioContraseniaDto {

    private String token;
    private String nuevaContrasenia;
}
