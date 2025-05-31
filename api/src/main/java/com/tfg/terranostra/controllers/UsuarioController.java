package com.tfg.terranostra.controllers;

import com.tfg.terranostra.dto.UsuarioDto;
import com.tfg.terranostra.models.UsuarioModel;
import com.tfg.terranostra.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")

/**
 * Controlador para la gestión de usuarios en la aplicación.
 * Proporciona endpoints para registrar, obtener, actualizar y eliminar usuarios.
 *
 * @author ebp
 * @version 1.0
 */
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registra un nuevo usuario en la base de datos.
     * - Recibe los datos del usuario y los guarda en la base de datos.<br>
     * - Devuelve un código HTTP 201 si el usuario se registra correctamente.<br>
     * - En caso de error, devuelve un código HTTP 500 con un mensaje de error.
     *
     * @param userDto Objeto que contiene la información del usuario a registrar.
     * @return `ResponseEntity` con un mensaje de éxito o error.
     */

    @PostMapping("/registro")
    public ResponseEntity<String> aniadirUsuario(@RequestBody UsuarioDto userDto) {
        logger.info("📝 Intentando registrar un nuevo usuario: {}", userDto.getEmail());

        try {
            UsuarioModel nuevoUsuario = usuarioService.aniadirUsuario(userDto);
            logger.info("✅ Usuario registrado con éxito. ID: {}", nuevoUsuario.getId());
            return new ResponseEntity<>("Usuario registrado con éxito. ID: " + nuevoUsuario.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("❌ Error al registrar usuario: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error al añadir el usuario: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene la lista de todos los usuarios registrados en la base de datos.
     *
     * @return `ResponseEntity` con una lista de usuarios o un código HTTP 500 en caso de error.
     */


    @GetMapping
    public ResponseEntity<List<UsuarioDto>> obtenerUsuarios() {
        logger.info("📢 Solicitando lista de usuarios...");

        try {
            List<UsuarioDto> usuarios = usuarioService.obtenerTodos();
            logger.info("✅ Usuarios obtenidos con éxito. Total: {}", usuarios.size());
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.error("❌ Error al obtener usuarios: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene los detalles de un usuario a través de su correo electrónico.
     * - Si el usuario existe, devuelve sus datos con un código HTTP 200.<br>
     * - Si el usuario no se encuentra, devuelve un código HTTP 404.<br>
     * - En caso de error, devuelve un código HTTP 500.
     *
     * @param email Correo electrónico del usuario a buscar.
     * @return `ResponseEntity` con los datos del usuario o un mensaje de error.
     */


    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDto> obtenerUsuarioPorEmail(@PathVariable String email) {
        logger.info("🔎 Buscando usuario con email: {}", email);

        try {
            UsuarioDto usuario = usuarioService.obtenerPorEmail(email);
            if (usuario != null) {
                logger.info("✅ Usuario encontrado: {}", usuario);
                return ResponseEntity.ok(usuario);
            } else {
                logger.warn("⚠️ Usuario no encontrado con email: {}", email);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("❌ Error al buscar usuario por email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualiza la información de un usuario en la base de datos mediante su correo electrónico.
     * - Si el usuario existe, se actualizan sus datos y se devuelve un código HTTP 200.<br>
     * - Si el usuario no se encuentra, devuelve un código HTTP 404.<br>
     * - En caso de error, devuelve un código HTTP 500.
     *
     * @param email Correo electrónico del usuario a actualizar.
     * @param userDto Objeto con los nuevos datos del usuario.
     * @return `ResponseEntity` con un mensaje de éxito o error.
     */

    @PutMapping("/email/{email}")
    public ResponseEntity<String> actualizarUsuarioPorEmail(@PathVariable String email, @RequestBody UsuarioDto userDto) {
        logger.info("✏️ Intentando actualizar usuario con email: {}", email);
        logger.debug("📤 Datos recibidos para actualizar: {}", userDto);

        try {
            boolean actualizado = usuarioService.actualizarUsuarioPorEmail(email, userDto);
            if (actualizado) {
                logger.info("✅ Usuario actualizado correctamente: {}", email);
                return ResponseEntity.ok("Usuario actualizado con éxito");
            } else {
                logger.warn("⚠️ No se encontró usuario con email: {} para actualizar", email);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("❌ Error al actualizar usuario con email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar usuario");
        }
    }

    /**
     * Elimina un usuario de la base de datos mediante su correo electrónico.
     *
     * - Si el usuario existe y se elimina correctamente, devuelve un código HTTP 200.<br>
     * - Si el usuario no se encuentra, devuelve un código HTTP 404.<br>
     * - En caso de error, devuelve un código HTTP 500.*
     * @param email Correo electrónico del usuario a eliminar.
     *
     * @return `ResponseEntity` con un mensaje de éxito o error.
     */

    @DeleteMapping("/email/{email}")
    public ResponseEntity<String> eliminarUsuarioPorEmail(@PathVariable String email) {
        logger.info("🗑️ Intentando eliminar usuario con email: {}", email);

        try {
            boolean eliminado = usuarioService.eliminarUsuarioPorEmail(email);
            if (eliminado) {
                logger.info("✅ Usuario eliminado correctamente: {}", email);
                return ResponseEntity.ok("Usuario eliminado correctamente");
            } else {
                logger.warn("⚠️ No se encontró usuario con email: {} para eliminar", email);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("❌ Error al eliminar usuario con email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar usuario");
        }
    }

    @PostMapping("/solicitar-cambio")
    public ResponseEntity<String> solicitarCambioContrasenia(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        logger.info("📩 Solicitud de cambio de contraseña para: {}", email);

        try {
            usuarioService.enviarEnlaceCambioContrasenia(email);
            return ResponseEntity.ok("📨 Se ha enviado un enlace de cambio de contraseña.");
        } catch (Exception e) {
            logger.error("❌ Error al enviar enlace de recuperación: {}", e.getMessage());
            return ResponseEntity.status(404).body("❌ Usuario no encontrado con ese email.");
        }
    }
}
