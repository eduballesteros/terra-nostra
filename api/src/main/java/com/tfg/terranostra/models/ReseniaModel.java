package com.tfg.terranostra.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una reseña hecha por un usuario sobre un producto.
 *
 * Cada reseña está vinculada a un usuario y a un producto específicos, y contiene
 * un comentario, una valoración numérica y la fecha de publicación.
 *
 * La combinación de usuario y producto debe ser única, es decir,
 * un usuario solo puede dejar una reseña por producto.
 */
@Entity
@Table(name = "resenas") // 🔥 Eliminado uniqueConstraints
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReseniaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoModel producto;

    @Column(nullable = false)
    private String comentario;

    @Column(nullable = false)
    private int valoracion;

    @Column(nullable = false)
    private LocalDateTime fecha;
}

