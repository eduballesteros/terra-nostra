package com.tfg.terranostra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@Data
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Column
    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @Column(unique = true)
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @Column
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasenia;

    @Column
    private String direccion;

    @Column
    private String telefono;

    @Column(name = "correo_verificado")
    private Boolean correoVerificado;

    @Column
    private LocalDateTime fechaRegistro;

    @Column
    private LocalDateTime fechaModificacion;

    @Column
    private String rol;
}
