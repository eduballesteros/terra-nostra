package com.terra_nostra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa el carrito de compras de un usuario.
 *
 * Contiene el identificador del carrito, la relación con el usuario,
 * la fecha de creación y una lista de productos con su cantidad y precio.
 *
 * Este objeto se utiliza tanto para mostrar el estado del carrito en la interfaz
 * como para enviarlo/recibirlo desde la API.
 *
 *
 * @author ebp
 * @version 1.0
 */
@Data
public class CarritoDto {

    private Long id;

    private Long usuarioId;

    @NotNull
    private LocalDateTime fechaCreacion;

    @NotEmpty
    private List<@Valid CarritoItemDto> items;
}
