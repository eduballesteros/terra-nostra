package com.terra_nostra.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.terra_nostra.service.UsuarioService;
import com.terra_nostra.dto.UsuarioDto;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/admin")

/**
 * Controlador para la administración de usuarios en la aplicación.
 * Proporciona endpoints para eliminar y editar usuarios.
 *
 * @author ebp
 * @version 1.0
 */

public class AdminController {
    /**
     * Logger para registrar información sobre las operaciones de administración de usuarios.
     */

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    @Autowired
    private UsuarioService usuarioService; // Servicio para obtener usuarios

    /**
     * Elimina un usuario del sistema según su correo electrónico.
     *
     * @param email Correo electrónico del usuario a eliminar.
     * @return `ResponseEntity` con un mensaje indicando el resultado de la operación.
     */


    @DeleteMapping("/eliminarUsuario")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@RequestParam String email) {
        logger.info("🗑 Solicitando eliminación del usuario con email: {}", email);

        Map<String, String> respuesta = new HashMap<>();
        int statusCode = usuarioService.eliminarUsuarioDesdeAPI(email);

        switch (statusCode) {
            case 200 -> {
                respuesta.put("mensaje", "✅ Usuario eliminado con éxito.");
                return ResponseEntity.ok(respuesta);
            }
            case 403 -> {
                respuesta.put("mensaje", "⛔ No se puede eliminar al último administrador.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuesta);
            }
            case 409 -> {
                respuesta.put("mensaje", "⛔ El usuario tiene datos relacionados y no se puede eliminar.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
            }
            case 404 -> {
                respuesta.put("mensaje", "⚠️ Usuario no encontrado.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }
            default -> {
                respuesta.put("mensaje", "❌ Error inesperado al eliminar el usuario.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
            }
        }
    }



    /**
     * Actualiza la información de un usuario en el sistema.
     *
     * @param email Correo electrónico del usuario a actualizar.
     * @param usuario Objeto `UsuarioDto` con los nuevos datos del usuario.
     * @return `ResponseEntity` con un mensaje indicando el resultado de la operación.
     */


    @PutMapping("/editarUsuario")
    public ResponseEntity<Map<String, String>> editarUsuario(@RequestParam String email, @RequestBody UsuarioDto usuario) {
        logger.info("✏️ Solicitando actualización del usuario con email: {}", email);
        logger.debug("🔹 Datos recibidos: {}", usuario);

        boolean actualizado = usuarioService.actualizarUsuario(email, usuario);
        Map<String, String> respuesta = new HashMap<>();

        if (actualizado) {
            logger.info("✅ Usuario actualizado correctamente: {}", email);
            respuesta.put("mensaje", "✅ Usuario actualizado con éxito.");
            return ResponseEntity.ok(respuesta);
        } else {
            logger.error("❌ Error al actualizar usuario con email: {}", email);
            respuesta.put("mensaje", "❌ Error al actualizar usuario.");
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

}
