package com.tfg.terranostra.controllers;

import com.tfg.terranostra.models.CorreoVerificacionToken;
import com.tfg.terranostra.models.UsuarioModel;
import com.tfg.terranostra.repositories.CorreoVerificacionTokenRepository;
import com.tfg.terranostra.repositories.UsuarioRepository;
import com.tfg.terranostra.services.CorreoService;
import com.tfg.terranostra.services.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/verificacion")
public class VerificacionCorreoController {

    @Autowired
    private CorreoService correoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

   @Autowired
   private CorreoVerificacionTokenRepository tokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(VerificacionCorreoController.class);

    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<String> reenviarCorreoVerificacion(@RequestParam String email) {
        try {
            usuarioService.enviarEnlaceVerificacionCuenta(email);
            return ResponseEntity.ok("📩 Correo reenviado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al reenviar el correo.");
        }
    }

    @GetMapping("/confirmar")
    @Transactional
    public void verificarCorreo(@RequestParam("token") String token, HttpServletResponse response) throws IOException, MessagingException {
        logger.info("🔐 Verificando token recibido: {}", token);

        CorreoVerificacionToken tokenEntidad = tokenRepository.findByToken(token).orElse(null);
        if (tokenEntidad == null || tokenEntidad.getExpiracion().isBefore(LocalDateTime.now())) {
            logger.warn("❌ Token inválido o expirado");
            if (tokenEntidad != null) tokenRepository.delete(tokenEntidad);
            response.sendRedirect("http://localhost:8080/?verificacion=fallida");
            return;
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(tokenEntidad.getEmail()).orElse(null);
        if (usuario == null) {
            response.sendRedirect("http://localhost:8080/?verificacion=fallida");
            return;
        }

        usuario.setCorreoVerificado(true);
        usuarioRepository.save(usuario);
        tokenRepository.delete(tokenEntidad);

        logger.info("✅ Correo verificado correctamente para el usuario: {}", usuario.getEmail());

        // Enviar correo de confirmación
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

        // Redirigir al index.jsp del frontend
        response.sendRedirect("http://localhost:8080/?verificacion=exitosa");

    }
}
