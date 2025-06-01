package com.terra_nostra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
/**
 * DTO que representa los datos necesarios para crear una reseña
 * desde el cliente web y enviarla a la API REST.
 *
 * Incluye:
 * - Identificador del producto reseñado
 * - Identificador del usuario que la escribe
 * - Comentario textual
 * - Valoración numérica entre 1 y 5
 *
 * Las validaciones aseguran que la reseña sea coherente y útil.
 * Este DTO es utilizado por el frontend al enviar el formulario de reseñas.
 *
 * @author ebp
 * @version 1.0
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
