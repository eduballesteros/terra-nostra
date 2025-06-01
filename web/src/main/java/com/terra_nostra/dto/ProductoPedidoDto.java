package com.terra_nostra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO que representa un producto incluido dentro de un pedido.
 * Se utiliza para detallar los productos comprados por un usuario, incluyendo
 * la cantidad y el precio unitario en el momento de la compra.
 *
 * Este DTO se usa en la creación de pedidos y en la visualización del historial.
 *
 * @author ebp
 * @version 1.0
 */

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
