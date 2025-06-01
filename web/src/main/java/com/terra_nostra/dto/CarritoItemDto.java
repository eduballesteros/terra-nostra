package com.terra_nostra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO que representa un ítem individual dentro del carrito de compras.
 *
 * Contiene información mínima necesaria para identificar el producto,
 * su cantidad, nombre, imagen y precio unitario.
 *
 * Se utiliza en conjunto con {@link CarritoDto} para representar la estructura completa del carrito.
 * Incluye validaciones para asegurar consistencia al agregar o modificar ítems.
 *
 * @author ebp
 * @version 1.0
 */
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
