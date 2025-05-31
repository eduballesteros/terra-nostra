package com.terra_nostra.controller;

import com.terra_nostra.service.RecuperacionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

@Controller
public class RecuperacionController {

    @Autowired
    private RecuperacionService recuperacionService;

    /**
     * Muestra el formulario para cambiar la contraseña.
     * Este método se invoca cuando el usuario hace clic en el enlace del correo.
     */
    @GetMapping("/cambiar-password")
    public String mostrarFormulario(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "cambiar-password"; // Vista JSP
    }

    /**
     * Procesa el formulario para cambiar la contraseña
     */
    @PostMapping("/cambiar-password")
    @ResponseBody
    public ResponseEntity<?> procesarCambioPassword(@RequestParam("token") String token,
                                                    @RequestParam("nuevaContrasenia") String nuevaContrasenia) {
        boolean exito = recuperacionService.cambiarContrasenia(token, nuevaContrasenia);

        if (exito) {
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "✅ Tu contraseña ha sido cambiada exitosamente."));
        } else {
            return ResponseEntity.status(400).body(Collections.singletonMap("error", "❌ No se pudo cambiar la contraseña. Es posible que el token haya expirado."));
        }
    }


    /**
     * Enviar enlace de recuperación por correo al usuario logueado.
     */
    @PostMapping("/recuperacion/enviar-enlace")
    @ResponseBody
    public ResponseEntity<String> enviarEnlaceAjax(@RequestParam("email") String email) {
        boolean enviado = recuperacionService.solicitarCambioContrasenia(email);

        if (enviado) {
            return ResponseEntity.ok("📬 Enlace de recuperación enviado al correo.");
        } else {
            return ResponseEntity.status(500).body("❌ No se pudo enviar el enlace de recuperación.");
        }
    }
}
