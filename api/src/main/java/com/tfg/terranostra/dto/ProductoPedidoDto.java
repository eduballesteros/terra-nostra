package com.tfg.terranostra.dto;

import lombok.Data;

@Data
public class ProductoPedidoDto {
    private Long productoId;
    private int cantidad;
    private double precioUnitario;
    private String nombre;
}
