package com.tfg.terranostra.controllers;

import com.tfg.terranostra.models.CorreoVerificacionToken;
import com.tfg.terranostra.models.UsuarioModel;
import com.tfg.terranostra.repositories.CorreoVerificacionTokenRepository;
import com.tfg.terranostra.repositories.UsuarioRepository;
import com.tfg.terranostra.services.CorreoService;
import com.tfg.terranostra.services.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/verificacion")

/**
 * Controlador REST encargado de gestionar la verificación de cuentas de usuario
 * a través de enlaces enviados por correo electrónico.
 *
 * Funcionalidades:
 * - Reenviar correos de verificación.
 * - Confirmar tokens de verificación y activar la cuenta.
 *
 * Este proceso garantiza que solo usuarios con correos válidos puedan activar su cuenta
 * y acceder a las funcionalidades de la plataforma.
 *
 * @author ebp
 * @version 1.0
 */

public class VerificacionCorreoController {

    private static final Logger logger = LoggerFactory.getLogger(VerificacionCorreoController.class);

    @Autowired
    private CorreoService correoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CorreoVerificacionTokenRepository tokenRepository;

    /**
     * Reenvía un correo de verificación al email indicado.
     * Ruta: POST /api/verificacion/reenviar-verificacion?email=<correo>
     */
    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<String> reenviarCorreoVerificacion(@RequestParam String email) {
        try {
            usuarioService.enviarEnlaceVerificacionCuenta(email);
            return ResponseEntity.ok("📩 Correo de verificación reenviado con éxito.");
        } catch (Exception e) {
            logger.error("❌ Error al reenviar correo de verificación para {}: {}", email, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error interno al reenviar el correo de verificación.");
        }
    }

    /**
     * Confirma un token de verificación.
     * Ruta: GET /api/verificacion/confirmar?token=<token>
     * Luego redirige al frontend (producción) con ?verificacion=exitosa o ?verificacion=fallida
     */
    @GetMapping("/confirmar")
    @Transactional
    public void verificarCorreo(@RequestParam("token") String token, HttpServletResponse response)
            throws IOException, MessagingException {
        logger.info("🔐 Verificando token recibido: {}", token);

        CorreoVerificacionToken tokenEntidad = tokenRepository.findByToken(token).orElse(null);

        if (tokenEntidad == null) {
            logger.warn("❌ Token no encontrado: {}", token);
            response.sendRedirect("https://terra-nostra.eduballesterosperez.com/index.jsp?verificacion=fallida");
            return;
        }

        if (tokenEntidad.getExpiracion().isBefore(LocalDateTime.now())) {
            logger.warn("❌ Token expirado: {}", token);
            tokenRepository.delete(tokenEntidad);
            response.sendRedirect("https://terra-nostra.eduballesterosperez.com/index.jsp?verificacion=fallida");
            return;
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(tokenEntidad.getEmail()).orElse(null);
        if (usuario == null) {
            logger.warn("❌ No se encontró usuario para el email del token: {}", tokenEntidad.getEmail());
            response.sendRedirect("https://terra-nostra.eduballesterosperez.com/index.jsp?verificacion=fallida");
            return;
        }

        usuario.setCorreoVerificado(true);
        usuarioRepository.save(usuario);
        tokenRepository.delete(tokenEntidad);
        logger.info("✅ Correo verificado correctamente para el usuario: {}", usuario.getEmail());

        String asunto = "✅ ¡Tu cuenta en Terra Nostra ha sido verificada!";
        String cuerpoHtml = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                <h2 style="color: #28a745;">✅ Verificación exitosa</h2>
                <p>Hola <strong>%s</strong>,</p>
                <p>Tu cuenta ha sido verificada correctamente. ¡Gracias por unirte a Terra Nostra!</p>
                <p>Ya puedes iniciar sesión y disfrutar de todos los servicios.</p>
                <hr style="margin-top: 30px;">
                <p style="font-size: 12px; color: #888;">
                    © 2025 Terra Nostra · Este correo es automático, no respondas.
                </p>
            </div>
            """.formatted(usuario.getNombre());
        correoService.enviarCorreo(usuario.getEmail(), asunto, cuerpoHtml);

        response.sendRedirect("https://terra-nostra.eduballesterosperez.com/index.jsp?verificacion=exitosa");
    }
}
