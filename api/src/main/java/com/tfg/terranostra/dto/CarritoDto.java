package com.tfg.terranostra.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa el carrito de un usuario,
 * incluyendo su ID, fecha de creación y los productos agregados.
 */

@Data
public class CarritoDto {

    private Long id;

    private Long usuarioId;

    private LocalDateTime fechaCreacion;

    private List<CarritoItemDto> items;
}
