package com.tfg.terranostra.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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