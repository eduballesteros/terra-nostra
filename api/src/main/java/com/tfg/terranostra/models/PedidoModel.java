package com.tfg.terranostra.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoModel {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @Column
    private LocalDateTime fecha;

    @Column
    private BigDecimal total;

    @Column
    private String emailUsuario;

    @Column
    private String estado;

    @Column
    private String metodoPago;

    @Column
    private String direccionEnvio;

    @Column
    private String telefonoContacto;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoPedidoModel> listaProductos;
}
