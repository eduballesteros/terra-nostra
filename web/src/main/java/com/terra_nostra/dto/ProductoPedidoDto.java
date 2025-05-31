package com.terra_nostra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoPedidoDto {

    @NotNull
    private Long productoId;

    @Min(1)
    private int cantidad;

    @NotBlank
    private String nombre;

    @Positive
    private double precioUnitario;
}
