package com.tfg.terranostra.controllers;

import com.tfg.terranostra.dto.CrearPedidoDto;
import com.tfg.terranostra.dto.PedidoDto;
import com.tfg.terranostra.models.PedidoModel;
import com.tfg.terranostra.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")

/**
 * Controlador para la gestión de pedidos en la aplicación.
 * Permite crear pedidos y consultar pedidos existentes por usuario o por ID.
 *
 * @author ebp
 * @version 1.0
 */

public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Crea un nuevo pedido a partir de la información recibida en el DTO.
     *
     * Este método se utiliza al finalizar una compra. Toma los datos del usuario, los productos
     * en el carrito, información de envío y pago, y genera un nuevo pedido persistido en la base de datos.
     *
     * @param dto Objeto {@link CrearPedidoDto} que contiene los datos necesarios para registrar el pedido:
     *            productos, cantidades, dirección de envío, usuario, método de pago, etc.
     * @return `ResponseEntity` con el objeto {@link PedidoDto} que representa el pedido creado,
     *         incluyendo su identificador, fecha de creación, total, y detalles de los productos.
     */

    @PostMapping
    public ResponseEntity<PedidoDto> crearPedido(@RequestBody CrearPedidoDto dto) {
        PedidoModel nuevoPedido = pedidoService.crearPedido(dto);
        PedidoDto respuesta = pedidoService.convertirAPedidoDto(nuevoPedido);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Recupera todos los pedidos realizados por un usuario determinado.
     *
     * Este endpoint permite mostrar el historial de compras del usuario desde su perfil,
     * agrupando todos los pedidos anteriores con sus respectivos datos.
     *
     * @param usuarioId Identificador único del usuario del cual se desea obtener el historial de pedidos.
     * @return `ResponseEntity` con una lista de objetos {@link PedidoDto},
     *         cada uno representando un pedido realizado por el usuario.
     */

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDto>> obtenerPedidosPorUsuario(@PathVariable Long usuarioId) {
        List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtiene los detalles de un pedido específico a través de su identificador.
     *
     * Este método se puede utilizar para mostrar un resumen ampliado de un pedido concreto,
     * por ejemplo, desde el detalle en el perfil del usuario o para generación de facturas.
     *
     * @param id Identificador único del pedido.
     * @return `ResponseEntity` con el objeto {@link PedidoDto} que contiene la información completa del pedido:
     *         productos comprados, cantidades, precios, dirección, fecha, etc.
     */

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> obtenerPedidoPorId(@PathVariable Long id) {
        PedidoDto dto = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(dto);
    }


}