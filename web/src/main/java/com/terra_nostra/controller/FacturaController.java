package com.terra_nostra.controller;

import com.terra_nostra.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/factura")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    /**
     * Solicita la factura de un pedido y la envía por email.
     * @param pedidoId ID del pedido
     * @param email    Email de destino
     */
    @PostMapping("/enviar")
    public ResponseEntity<String> enviarFactura(@RequestParam Long pedidoId, @RequestParam String email) {
        try {
            facturaService.enviarFacturaPorCorreo(pedidoId, email);
            return ResponseEntity.ok("📩 Factura enviada correctamente a " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al enviar factura: " + e.getMessage());
        }
    }
}
