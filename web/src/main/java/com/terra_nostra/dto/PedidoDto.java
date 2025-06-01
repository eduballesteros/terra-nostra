package com.terra_nostra.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa los detalles de un pedido realizado por un usuario.
 *
 * Incluye información del usuario, datos de contacto y envío,
 * así como los productos adquiridos y el estado del pedido.
 *
 * Este DTO se utiliza principalmente para enviar información al frontend,
 * como por ejemplo en la sección de "Mis pedidos".
 *
 * @author ebp
 * @version 1.0
 */


@Data
public class PedidoDto {

    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private String emailUsuario;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private String metodoPago;
    private String direccionEnvio;
    private String telefonoContacto;
    private List<ProductoPedidoDto> productos;

}