package com.tfg.terranostra.dto;

import lombok.Data;

/**
 * DTO de entrada para crear una nueva reseña (resenia).
 *
 * Recoge los datos enviados por el cliente para registrar una resenia
 * asociada a un producto y un usuario.
 */
@Data
public class CrearReseniaDto {

    /**
     * ID del producto al que se hace la resenia.
     */
    private Long productoId;

    /**
     * ID del usuario que realiza la resenia.
     */
    private Long usuarioId;

    private String comentario;

    private int valoracion;
}
