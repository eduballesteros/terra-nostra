package com.tfg.terranostra.controllers;

import com.tfg.terranostra.dto.CarritoDto;
import com.tfg.terranostra.dto.CarritoItemDto;
import com.tfg.terranostra.dto.CrearPedidoDto;
import com.tfg.terranostra.services.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    /**
     * Devuelve el carrito actual del usuario.
     *
     * @param usuarioId ID del usuario
     * @return DTO con el contenido del carrito
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerCarrito(@PathVariable Long usuarioId) {
        try {
            CarritoDto carrito = carritoService.obtenerCarritoPorUsuario(usuarioId);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudo cargar el carrito"));
        }
    }

    /**
     * Agrega un producto al carrito del usuario.
     *
     * @param usuarioId ID del usuario
     * @param item      Datos del producto a añadir
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestParam Long usuarioId,
                                              @RequestBody CarritoItemDto item) {
        try {
            if (item == null) {
                System.out.println("❌ El objeto CarritoItemDto es null");
                return ResponseEntity.badRequest().body(Map.of("error", "Item no válido"));
            }

            carritoService.agregarProductoAlCarrito(usuarioId, item);

            System.out.println("✅ Producto añadido correctamente al carrito.");
            return ResponseEntity.ok(Map.of("mensaje", "Producto añadido al carrito"));
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Error controlado: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Error inesperado al agregar producto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al agregar el producto al carrito"));
        }
    }

    /**
     * Elimina todos los productos del carrito del usuario.
     *
     * @param usuarioId ID del usuario
     */
    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Long usuarioId) {
        try {
            carritoService.vaciarCarrito(usuarioId);
            return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudo vaciar el carrito"));
        }
    }

    /**
     * Ajusta la cantidad de un producto en el carrito.
     * Si cantidad = 0, lo elimina.
     */
    @PutMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> actualizarCantidadProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer nuevaCantidad = body.get("cantidad");
            if (nuevaCantidad == null || nuevaCantidad < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cantidad no válida"));
            }

            carritoService.actualizarCantidadProducto(usuarioId, productoId, nuevaCantidad);
            return ResponseEntity.ok(Map.of("mensaje", "Cantidad actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
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
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error eliminando el producto"));
        }
    }

    /**
     * Finaliza la compra: convierte el carrito del usuario en un pedido y lo guarda.
     *
     * @param dto DTO con los datos de envío y pago del pedido
     * @return Confirmación de pedido
     */
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarCompra(@RequestBody CrearPedidoDto dto) {
        try {
            carritoService.finalizarCompraDesdeCarrito(dto);
            return ResponseEntity.ok(Map.of("mensaje", "✅ Pedido realizado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "❌ Error al finalizar la compra"));
        }
    }
}
