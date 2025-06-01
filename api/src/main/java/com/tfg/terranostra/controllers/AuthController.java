package com.tfg.terranostra.controllers;

import com.tfg.terranostra.dto.CambioContraseniaDto;
import com.tfg.terranostra.dto.LoginDto;
import com.tfg.terranostra.dto.TokenGoogleDto;
import com.tfg.terranostra.dto.UsuarioDto;
import com.tfg.terranostra.repositories.UsuarioRepository;
import com.tfg.terranostra.services.AuthService;
import com.tfg.terranostra.services.PasswordResetTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")

/**
 * Controlador para la autenticación de usuarios en la aplicación.
 * Proporciona endpoints para el inicio de sesión de los usuarios.
 *
 * @author ebp
 * @version 1.0
 */
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;


    /**
     * Maneja el inicio de sesión de los usuarios.
     * - Recibe un objeto `LoginDto` con las credenciales del usuario.<br>
     * - Si las credenciales son correctas, devuelve un objeto `UsuarioDto` con los datos del usuario autenticado.<br>
     * - Si las credenciales son incorrectas, devuelve un código de estado HTTP 401 con un mensaje de error.
     *
     * @param loginDto Objeto que contiene el correo electrónico y la contraseña del usuario.
     * @return `ResponseEntity` con el objeto `UsuarioDto` si la autenticación es exitosa,
     *         o un mensaje de error si las credenciales son incorrectas.
     */

    @PostMapping("/login")
    public ResponseEntity<?> inicioSesion(@RequestBody LoginDto loginDto) {
        logger.info("🔐 Intento de inicio de sesión para el usuario: {}", loginDto.getEmail());

        UsuarioDto usuarioDto = authService.autenticarUsuario(loginDto);

        if (usuarioDto == null) {
            logger.warn("❌ Inicio de sesión fallido: Credenciales incorrectas para el usuario {}", loginDto.getEmail());
            return ResponseEntity.status(401).body("❌ Credenciales incorrectas");
        }

        logger.info("✅ Inicio de sesión exitoso para el usuario: {}", loginDto.getEmail());
        logger.debug("📤 Respuesta JSON enviada por la API: {}", usuarioDto);

        return ResponseEntity.ok(usuarioDto);
    }

    /**
     * Valida si un token de restablecimiento de contraseña es válido.
     * - El token se pasa como parámetro en la URL.
     * - El servicio valida si el token existe, no ha expirado y está asociado a un usuario.
     *
     * @param token El token de restablecimiento de contraseña.
     * @return `ResponseEntity` con `true` si el token es válido, o `false` si no lo es.
     */

    @GetMapping("/validar-token")
    public ResponseEntity<Boolean> validarToken(@RequestParam String token) {
        boolean esValido = passwordResetTokenService.validarToken(token);
        return ResponseEntity.ok(esValido);
    }

    /**
     * Cambia la contraseña de un usuario usando un token de restablecimiento.
     * - El token y la nueva contraseña se reciben en el cuerpo del mensaje.
     * - El servicio valida el token y actualiza la contraseña si es válido.
     *
     * @param cambioContraseniaDto Objeto que contiene el token y la nueva contraseña.
     * @return `ResponseEntity` con un mensaje indicando si el cambio fue exitoso o no.
     */

    @PutMapping("/cambiar-password")
    public ResponseEntity<String> cambiarContrasenia(@RequestBody CambioContraseniaDto cambioContraseniaDto) {
        boolean exito = authService.cambiarContrasenia(cambioContraseniaDto);

        if (exito) {
            return ResponseEntity.ok("✅ Contraseña cambiada exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Token inválido o expirado.");
        }
    }

    /**
     * Autentica a un usuario usando un token de Google OAuth2.
     * - Recibe un token de Google en el cuerpo de la petición.
     * - Si el token es válido y el usuario existe (o se crea), se devuelve un `UsuarioDto` con la sesión iniciada.
     *
     * @param tokenGoogleDto Objeto que contiene el token JWT emitido por Google.
     * @return `ResponseEntity` con la información del usuario autenticado, o un error si falla el proceso.
     */

    @PostMapping("/login-google")
    public ResponseEntity<?> loginConGoogle(@RequestBody TokenGoogleDto tokenGoogleDto) {
        try {
            return authService.loginConGoogle(tokenGoogleDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en el login con Google: " + e.getMessage());
        }
    }
}
