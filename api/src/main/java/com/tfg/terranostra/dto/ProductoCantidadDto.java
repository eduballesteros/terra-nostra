package com.tfg.terranostra.dto;

import lombok.Data;

/**
 * DTO que representa la relación entre un producto y su cantidad.
 * Se utiliza, por ejemplo, para actualizar cantidades en el carrito
 * o validar stock disponible antes de confirmar un pedido.
 */

@Data
public class ProductoCantidadDto {
    private Long productoId;
    private int cantidad;
}
