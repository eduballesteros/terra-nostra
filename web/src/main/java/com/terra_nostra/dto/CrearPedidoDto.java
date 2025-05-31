package com.terra_nostra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CrearPedidoDto {

    @NotNull
    private Long usuarioId;

    @NotBlank
    @Email
    private String emailUsuario;

    @NotBlank
    private String metodoPago;

    @NotBlank
    private String direccionEnvio;

    @NotBlank
    private String telefonoContacto;

    @NotEmpty
    private List<@Valid ProductoPedidoDto> productos;
}
