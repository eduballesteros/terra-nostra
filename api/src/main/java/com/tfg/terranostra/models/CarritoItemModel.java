package com.tfg.terranostra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "carrito_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @NotNull(message = "El carrito no puede ser nulo")
    private CarritoModel carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto no puede ser nulo")
    private ProductoModel producto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "imagen", columnDefinition = "LONGTEXT")
    private String imagen;
}
