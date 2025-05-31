package com.terra_nostra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO que representa los datos necesarios para crear una resenia
 * desde el cliente web y enviarla a la API REST.
 */
@Data
public class CrearReseniaDto {

    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long productoId;

    @NotNull(message = "El ID del usuario no puede ser nulo.")
    private Long usuarioId;

    @NotBlank(message = "El comentario no puede estar vacío.")
    private String comentario;

    @Min(value = 1, message = "La valoración mínima es 1.")
    @Max(value = 5, message = "La valoración máxima es 5.")
    private int valoracion;
}
