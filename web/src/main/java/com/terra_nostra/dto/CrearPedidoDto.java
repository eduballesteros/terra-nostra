package com.terra_nostra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * DTO que representa los datos necesarios para crear un pedido.
 *
 * Este objeto se envía desde el frontend al finalizar la compra, y contiene:
 * - Información del usuario.
 * - Datos de contacto y envío.
 * - Lista de productos con cantidades y precios.
 *
 * Incluye validaciones para asegurar que todos los campos obligatorios estén presentes
 * y en un formato adecuado.
 *
 * @author ebp
 * @version 1.0
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
