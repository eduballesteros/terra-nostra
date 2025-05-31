package com.terra_nostra.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de salida para mostrar una resenia recibida desde la API,
 * con datos del usuario, producto, comentario y fecha.
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
