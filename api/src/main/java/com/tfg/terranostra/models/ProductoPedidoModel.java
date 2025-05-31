package com.tfg.terranostra.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



@Entity
@Table(name = "producto_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoModel pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoModel producto;

    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private java.math.BigDecimal precioUnitario;
}

