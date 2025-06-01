package com.tfg.terranostra.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO utilizado para crear un nuevo pedido desde el cliente.
 * Contiene los datos del usuario, detalles del envío y productos del carrito.
 */

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
