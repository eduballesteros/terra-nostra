package com.terra_nostra.controller;

import com.paypal.orders.Order;
import com.terra_nostra.dto.CarritoDto;
import com.terra_nostra.dto.PedidoDto;
import com.terra_nostra.service.CarritoService;
import com.terra_nostra.service.PayPalService;
import com.terra_nostra.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Controlador que gestiona el flujo de pago y pedidos en Terra Nostra desde el proyecto web.
 *
 * Incluye la integración con PayPal y el procesamiento del carrito tras la confirmación del pago.
 * También permite consultar los pedidos del usuario autenticado.
 *
 * Este controlador trabaja con sesiones HTTP y redirecciones a vistas JSP.
 *
 * @author ebp
 * @version 1.0
 */
@Controller
public class PedidoController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Inicia el proceso de checkout con PayPal.
     *
     * Recibe los datos de envío y el ID del usuario, calcula el total del carrito,
     * genera una orden de PayPal y devuelve la URL de aprobación.
     *
     * @param body Contiene `usuarioId` y los datos de envío.
     * @param session Sesión HTTP para guardar los datos temporales de envío.
     * @return Mapa con clave `paypalUrl` que contiene la URL de aprobación.
     */
    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, String> iniciarCheckout(@RequestBody Map<String, Object> body, HttpSession session) {
        try {
            Long usuarioId = Long.parseLong(body.get("usuarioId").toString());
            session.setAttribute("envio", body.get("envio"));

            CarritoDto carrito = carritoService.obtenerCarrito(usuarioId);
            double total = carrito.getItems().stream()
                    .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                    .sum();

            String urlAprobacion = payPalService.crearOrden(total);
            return Map.of("paypalUrl", urlAprobacion);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al iniciar el pago");
        }
    }

    /**
     * Endpoint que se ejecuta tras una confirmación exitosa de PayPal.
     *
     * Captura el pago, procesa el pedido a partir del carrito y limpia la sesión.
     * Redirige a la página de inicio con el parámetro `pago=exito`.
     *
     * @param orderId ID de la orden de PayPal.
     * @param usuarioId ID del usuario (como string desde parámetro de redirección).
     * @param session Sesión HTTP con los datos de envío temporales.
     * @return Redirección a `/terra-nostra?pago=exito` o a `?pago=error` si falla.
     */
    @GetMapping("/pago/exitoso")
    public String pagoExitoso(@RequestParam("token") String orderId,
                              @RequestParam(required = false) String usuarioId,
                              HttpSession session) {
        try {
            Order orden = payPalService.capturarPago(orderId);

            if (usuarioId == null) return "redirect:/terra-nostra?pago=error";
            Long uid = Long.parseLong(usuarioId);

            CarritoDto carrito = carritoService.obtenerCarrito(uid);
            @SuppressWarnings("unchecked")
            Map<String, String> envio = (Map<String, String>) session.getAttribute("envio");
            if (envio == null) return "redirect:/terra-nostra?pago=error";

            carritoService.procesarPedidoTrasPago(uid, envio);
            session.removeAttribute("envio");

            return "redirect:/terra-nostra?pago=exito";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/terra-nostra?pago=error";
        }
    }

    /**
     * Devuelve todos los pedidos realizados por un usuario.
     *
     * @param usuarioId ID del usuario autenticado.
     * @return Lista de objetos `PedidoDto` o error 500 si falla.
     */
    @GetMapping("/usuario/pedidos")
    public ResponseEntity<List<PedidoDto>> obtenerPedidosUsuario(@RequestParam Long usuarioId) {
        try {
            List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Muestra una redirección a la página principal con error de pago.
     *
     * @return Redirección con parámetro `pago=error`.
     */
    @GetMapping("/pago/error")
    public String pagoError() {
        return "redirect:/terra-nostra?pago=error";
    }

    /**
     * Muestra una redirección a la página principal si el usuario cancela el pago.
     *
     * @return Redirección con parámetro `pago=cancelado`.
     */
    @GetMapping("/pago/cancelado")
    public String pagoCancelado() {
        return "redirect:/terra-nostra?pago=cancelado";
    }
}
