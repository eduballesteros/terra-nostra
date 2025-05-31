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

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerCarrito(@PathVariable Long usuarioId) {
        try {
            CarritoDto dto = carritoService.obtenerCarrito(usuarioId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al obtener el carrito"));
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestParam Long usuarioId, @RequestBody String rawJson) throws Exception {

        CarritoItemDto item = new ObjectMapper().readValue(rawJson, CarritoItemDto.class);

        carritoService.agregarProducto(usuarioId, item);
        return ResponseEntity.ok(Map.of("mensaje", "ok"));
    }



    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Long usuarioId) {
        try {
            carritoService.vaciarCarrito(usuarioId);
            return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al vaciar el carrito"));
        }
    }

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
     * Ajusta la cantidad de un producto en el carrito.
     * Espera un body JSON: { "cantidad": nuevoValor }
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
     * Elimina por completo un producto del carrito.
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

