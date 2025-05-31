package com.tfg.terranostra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @Column(name = "descripcion_breve", nullable = false, length = 255)
    @NotBlank(message = "La descripción breve no puede estar vacía.")
    private String descripcionBreve;

    @Lob
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    @NotNull(message = "El precio no puede estar vacío.")
    @Positive(message = "El precio debe ser un valor positivo.")
    private BigDecimal precio;

    @Column(nullable = false)
    @NotNull(message = "La cantidad en stock no puede estar vacía.")
    @PositiveOrZero(message = "La cantidad en stock debe ser un valor positivo o cero.")
    private int stock;

    @Column
    @NotBlank(message = "La categoría no puede estar vacía.")
    private String categoria;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagen;

    @Column(precision = 5, scale = 2)
    @PositiveOrZero(message = "El descuento debe ser un valor positivo.")
    private BigDecimal descuento;

    @Column(name = "fecha_alta", updatable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        fechaAlta = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}
