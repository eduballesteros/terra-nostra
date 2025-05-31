package com.tfg.terranostra.dto;

import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductoDto {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción breve no puede estar vacía")
    private String descripcionBreve;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El precio no puede estar vacío")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;

    @NotNull(message = "La cantidad en stock no puede estar vacía")
    @PositiveOrZero(message = "La cantidad en stock debe ser positiva")
    private int stock;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagen;

    @PositiveOrZero(message = "El descuento debe ser un valor positivo o cero")
    private BigDecimal descuento;

    private LocalDateTime fechaAlta;

    private LocalDateTime fechaModificacion;
}
