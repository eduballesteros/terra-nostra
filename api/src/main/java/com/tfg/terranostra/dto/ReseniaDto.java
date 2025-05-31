package com.tfg.terranostra.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de salida para devolver una resenia al frontend.
 *
 * Contiene la información transformada y limpia que se quiere mostrar
 * al usuario, sin exponer entidades completas.
 */
@Data
public class ReseniaDto {

    private Long id;

    private Long productoId;
    private String nombreProducto;

    private Long usuarioId;
    private String nombreUsuario;

    private String comentario;

    private int valoracion;

    private LocalDateTime fecha;
}
