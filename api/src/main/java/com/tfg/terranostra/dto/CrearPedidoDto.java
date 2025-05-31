package com.tfg.terranostra.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
