package com.terra_nostra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terra_nostra.dto.CarritoDto;
import com.terra_nostra.dto.CarritoItemDto;
import com.terra_nostra.dto.CrearPedidoDto;
import com.terra_nostra.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST del proyecto web dinámico encargado de gestionar las operaciones
 * del carrito de compras del usuario desde la interfaz JSP.
 *
 * Permite:
 * - Ver el carrito
 * - Agregar productos
 * - Actualizar cantidades
 * - Eliminar productos
 * - Vaciar el carrito
 * - Finalizar compra (crear pedido)
 *
 * Este controlador se comunica con la capa de servicio que, a su vez,
 * interactúa con la API REST remota a través de HTTP.
 *
 * @author ebp
 * @version 1.0
 */
@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    /**
     * Obtiene el contenido actual del carrito para un usuario específico.
     *
     * @param usuarioId ID del usuario que está autenticado en la sesión.
     * @return `ResponseEntity` con el DTO del carrito si existe, o mensaje de error en caso de fallo.
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerCarrito(@PathVariable Long usuarioId) {
        try {
            CarritoDto dto = carritoService.obtenerCarrito(usuarioId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al obtener el carrito"));
        }
    }

    /**
     * Agrega un nuevo producto al carrito del usuario.
     *
     * El producto se recibe como JSON plano en el body y se convierte a `CarritoItemDto`.
     *
     * @param usuarioId ID del usuario que realiza la operación.
     * @param rawJson JSON crudo con los datos del producto (idProducto, cantidad).
     * @return `ResponseEntity` con mensaje de éxito o error.
     * @throws Exception si ocurre un fallo al deserializar o en el servicio.
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestParam Long usuarioId, @RequestBody String rawJson) throws Exception {
        CarritoItemDto item = new ObjectMapper().readValue(rawJson, CarritoItemDto.class);
        carritoService.agregarProducto(usuarioId, item);
        return ResponseEntity.ok(Map.of("mensaje", "ok"));
    }

    /**
     * Vacía completamente el carrito del usuario, eliminando todos los productos.
     *
     * @param usuarioId ID del usuario cuyo carrito será vaciado.
     * @return `ResponseEntity` con mensaje de confirmación o error.
     */
    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Long usuarioId) {
        try {
            carritoService.vaciarCarrito(usuarioId);
            return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al vaciar el carrito"));
        }
    }

    /**
     * Finaliza la compra del usuario, transformando su carrito en un pedido registrado.
     *
     * @param dto DTO que contiene los datos necesarios para registrar el pedido.
     * @return `ResponseEntity` con mensaje de éxito o error detallado si algo falla.
     */
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarCompra(@RequestBody CrearPedidoDto dto) {
        try {
            carritoService.finalizarCompra(dto);
            return ResponseEntity.ok(Map.of("mensaje", "✅ Pedido realizado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "❌ Error al finalizar la compra", "detalle", e.getMessage()));
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito.
     *
     * Si la cantidad es cero, puede implicar la eliminación del producto (según lógica interna).
     *
     * @param usuarioId ID del usuario que realiza la modificación.
     * @param productoId ID del producto cuya cantidad será modificada.
     * @param body JSON con clave "cantidad" y el nuevo valor.
     * @return `ResponseEntity` con mensaje de éxito o error detallado.
     */
    @PutMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> actualizarCantidadProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer nuevaCantidad = body.get("cantidad");
            if (nuevaCantidad == null || nuevaCantidad < 0) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Cantidad no válida"));
            }

            carritoService.actualizarCantidad(usuarioId, productoId, nuevaCantidad);
            return ResponseEntity.ok(Map.of("mensaje", "Cantidad actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", "Error actualizando la cantidad"));
        }
    }

    /**
     * Elimina completamente un producto del carrito del usuario.
     *
     * @param usuarioId ID del usuario.
     * @param productoId ID del producto a eliminar.
     * @return `ResponseEntity` con mensaje indicando el resultado de la operación.
     */
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> eliminarProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId) {
        try {
            carritoService.eliminarProducto(usuarioId, productoId);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", "Error eliminando el producto"));
        }
    }
}
