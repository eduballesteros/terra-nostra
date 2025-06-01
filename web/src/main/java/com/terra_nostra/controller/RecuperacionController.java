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

/**
 * Controlador encargado de gestionar la recuperación de contraseñas.
 *
 * Funcionalidades:
 * - Mostrar formulario de cambio de contraseña tras seguir enlace de correo.
 * - Procesar el cambio de contraseña.
 * - Enviar enlace de recuperación a través de correo electrónico.
 *
 * Este controlador trabaja con tokens temporales y utiliza vistas JSP para el formulario.
 *
 * @author ebp
 * @version 1.0
 */
@Controller
public class RecuperacionController {

    @Autowired
    private RecuperacionService recuperacionService;

    /**
     * Muestra el formulario para cambiar la contraseña cuando el usuario accede
     * desde el enlace de recuperación enviado por correo.
     *
     * @param token Token recibido en el enlace de recuperación.
     * @param model Modelo para inyectar el token en la vista.
     * @return Nombre de la vista JSP `cambiar-password`.
     */
    @GetMapping("/cambiar-password")
    public String mostrarFormulario(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "cambiar-password";
    }

    /**
     * Procesa el formulario para actualizar la contraseña usando el token de recuperación.
     *
     * @param token Token válido que autoriza el cambio de contraseña.
     * @param nuevaContrasenia Nueva contraseña introducida por el usuario.
     * @return `ResponseEntity` con mensaje de éxito o error si el token es inválido o expiró.
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
     * Envía un enlace de recuperación al correo electrónico especificado.
     * Este enlace permitirá al usuario acceder al formulario de cambio de contraseña.
     *
     * @param email Dirección de correo electrónico del usuario.
     * @return `ResponseEntity` indicando si el envío fue exitoso o si ocurrió un error.
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
