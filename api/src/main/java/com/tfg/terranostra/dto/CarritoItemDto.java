package com.tfg.terranostra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarritoItemDto {

    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long productoId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    private String nombre;
    private String imagen;
    private Double precioUnitario;
}
