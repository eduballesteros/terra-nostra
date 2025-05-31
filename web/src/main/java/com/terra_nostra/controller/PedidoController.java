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

@Controller
public class PedidoController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, String> iniciarCheckout(@RequestBody Map<String, Object> body, HttpSession session) {
        try {
            Long usuarioId = Long.parseLong(body.get("usuarioId").toString());
            System.out.println("🔐 [POST /checkout] usuarioId: " + usuarioId);

            session.setAttribute("envio", body.get("envio"));
            System.out.println("📦 Datos de envío guardados en sesión: " + body.get("envio"));

            CarritoDto carrito = carritoService.obtenerCarrito(usuarioId);
            System.out.println("🛒 Carrito cargado con " + carrito.getItems().size() + " items");

            double total = carrito.getItems().stream()
                    .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                    .sum();

            String urlAprobacion = payPalService.crearOrden(total);
            System.out.println("✅ Orden PayPal creada. Redirigiendo a: " + urlAprobacion);

            return Map.of("paypalUrl", urlAprobacion);
        } catch (Exception e) {
            System.out.println("❌ Error en /checkout: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al iniciar el pago");
        }
    }

    @GetMapping("/pago/exitoso")
    public String pagoExitoso(@RequestParam("token") String orderId,
                              @RequestParam(required = false) String usuarioId,
                              HttpSession session) {
        try {
            Order orden = payPalService.capturarPago(orderId);
            System.out.println("💳 Pago capturado exitosamente");

            if (usuarioId == null) {
                System.out.println("⚠️ usuarioId es null");
                return "redirect:/terra-nostra?pago=error";

            }

            Long uid = Long.parseLong(usuarioId);
            CarritoDto carrito = carritoService.obtenerCarrito(uid);
            System.out.println("🛒 Carrito obtenido para el usuario " + uid);

            @SuppressWarnings("unchecked")
            Map<String, String> envio = (Map<String, String>) session.getAttribute("envio");

            if (envio == null) {
                System.out.println("⚠️ No hay datos de envío en sesión");
                return "redirect:/terra-nostra?pago=error";

            }

            System.out.println("📨 Envío recuperado de sesión: " + envio);
            carritoService.procesarPedidoTrasPago(uid, envio);
            System.out.println("✅ Pedido procesado correctamente");

            session.removeAttribute("envio");
            System.out.println("🧹 Datos de envío eliminados de sesión");

            return "redirect:/terra-nostra?pago=exito";

        } catch (Exception e) {
            System.out.println("❌ ERROR en /pago/exitoso:");
            e.printStackTrace();
            return "redirect:/terra-nostra?pago=error";

        }
    }

    @GetMapping("/usuario/pedidos")
    public ResponseEntity<List<PedidoDto>> obtenerPedidosUsuario(@RequestParam Long usuarioId) {
        try {
            List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

    }

    @GetMapping("/pago/error")
    public String pagoError() {
        System.out.println("❌ Error en el proceso de pago");
        return "redirect:/terra-nostra?pago=error";

    }

    @GetMapping("/pago/cancelado")
    public String pagoCancelado() {
        System.out.println("⛔ Pago cancelado por el usuario");
        return "redirect:/terra-nostra?pago=cancelado";

    }
}

