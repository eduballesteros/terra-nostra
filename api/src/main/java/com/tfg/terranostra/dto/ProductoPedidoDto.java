package com.tfg.terranostra.dto;

import lombok.Data;

/**
 * DTO que representa un producto incluido dentro de un pedido.
 * Contiene información básica necesaria para mostrar o procesar
 * el contenido de un pedido.
 */

@Data
public class ProductoPedidoDto {
    private Long productoId;
    private int cantidad;
    private double precioUnitario;
    private String nombre;
}
