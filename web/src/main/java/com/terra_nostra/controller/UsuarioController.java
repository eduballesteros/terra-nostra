package com.terra_nostra.controller;
import com.terra_nostra.dto.UsuarioDto;
import com.terra_nostra.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import com.terra_nostra.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/usuario")

/**
 * Controlador para la gestión de usuarios.
 * Proporciona endpoints para el registro, la consulta y la autenticación de usuarios.
 *
 * @author ebp
 * @version 1.0
 */

public class UsuarioController {

	@Autowired
	private JwtUtil jwtUtil;  // Para generar el token

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	/**
	 * Registra un nuevo usuario y genera un token de sesión.
	 *
	 * @param nombre Nombre del usuario.
	 * @param apellido Apellido del usuario.
	 * @param email Correo electrónico del usuario.
	 * @param telefono Número de teléfono del usuario (opcional).
	 * @param contrasenia Contraseña del usuario.
	 * @param session Objeto `HttpSession` para gestionar la sesión del usuario.
	 * @param response Objeto `HttpServletResponse` para agregar cookies a la respuesta.
	 * @return `ResponseEntity` con un mensaje de éxito o error en el registro.
	 */

	@PostMapping("/registrar")
	public ResponseEntity<?> registroUsuario(
			@RequestParam("nombre") String nombre,
			@RequestParam("apellido") String apellido,
			@RequestParam("email") String email,
			@RequestParam(value = "telefono", required = false) String telefono,
			@RequestParam("contrasenia") String contrasenia,
			HttpSession session, HttpServletResponse response) {

		logger.info("📌 El controlador ha recibido la solicitud de registro.");
		logger.info("📥 Nombre recibido: {}", nombre);
		logger.info("📥 Apellido recibido: {}", apellido);
		logger.info("📥 Email recibido: {}", email);
		logger.info("📥 Teléfono recibido: {}", telefono);
		logger.info("📥 Contraseña recibida: {}", contrasenia);

		try {
			UsuarioDto usuarioNuevo = new UsuarioDto();
			usuarioNuevo.setNombre(nombre);
			usuarioNuevo.setApellido(apellido);
			usuarioNuevo.setEmail(email);
			usuarioNuevo.setTelefono(telefono);
			usuarioNuevo.setFechaRegistro(LocalDateTime.now(ZoneId.of("Europe/Madrid")));
			usuarioNuevo.setRol("ROLE_USER");
			usuarioNuevo.setContrasenia(contrasenia);

			logger.info("📌 Fecha de registro asignada: {}", usuarioNuevo.getFechaRegistro());

			// Registrar el usuario a través del servicio
			String resultado = usuarioService.registrarUsuario(usuarioNuevo, session);
			logger.info("📌 Resultado del registro: {}", resultado);

			if ("success".equals(resultado)) {
				String token = jwtUtil.generarToken(
						email,
						usuarioNuevo.getRol(),
						usuarioNuevo.getNombre(),
						usuarioNuevo.isCorreoVerificado(),
						usuarioNuevo.getId()
				);
				logger.info("📌 Token generado: {}", token);
				logger.info("✅ Registro exitoso. Bienvenido a Terra Nostra!");

				/**
				 * Crea una cookie segura con el token de autenticación del usuario.
				 *
				 * - `HttpOnly`: Evita accesos desde JavaScript para mayor seguridad.
				 * - `Secure`: Solo se envía en conexiones HTTPS.
				 * - `Path("/")`: Disponible en todo el dominio.
				 * - `MaxAge(60 * 60 * 24)`: Expira en 1 día.
				 */


				Cookie cookie = new Cookie("SESSIONID", token);
				cookie.setHttpOnly(true);
				cookie.setSecure(true);
				cookie.setPath("/");
				cookie.setMaxAge(60 * 60 * 24);

				response.addCookie(cookie); // Agregar la cookie a la respuesta

				// Devolver solo un mensaje de éxito sin el token en el cuerpo de la respuesta
				return ResponseEntity.ok("{\"mensaje\": \"✅ Registro exitoso. Bienvenido a Terra Nostra!\"}");
			} else {
				logger.error("❌ Error en el registro.");
				return ResponseEntity.status(400).body("{\"mensaje\": \"❌ Error en el registro. Intenta nuevamente.\"}");
			}

		} catch (Exception e) {
			logger.error("❌ Error en el servidor: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("{\"mensaje\": \"❌ Error en el servidor. Intenta nuevamente.\"}");
		}

	}

	/**
	 * Obtiene la lista de todos los usuarios registrados en la base de datos.*
	 * @return `ResponseEntity` con la lista de usuarios en formato `UsuarioDto` o un estado `204 No Content` si no hay usuarios.
	 */


	@GetMapping("/listar")
	public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
		logger.info("📢 Solicitando lista de usuarios desde el controlador.");
		List<UsuarioDto> usuarios = usuarioService.obtenerTodosLosUsuarios();

		if (usuarios.isEmpty()) {
			logger.warn("⚠️ No hay usuarios registrados en la base de datos.");
			return ResponseEntity.noContent().build();
		} else {
			logger.info("✅ Usuarios obtenidos correctamente. Total: {}", usuarios.size());
			return ResponseEntity.ok(usuarios);
		}
	}

	@PutMapping("/actualizar")
	public ResponseEntity<?> actualizarUsuario(@RequestBody UsuarioDto usuarioDto) {
		try {
			logger.info("🛠 Recibida solicitud para actualizar usuario con email: {}", usuarioDto.getEmail());

			boolean actualizado = usuarioService.actualizarUsuario(usuarioDto.getEmail(), usuarioDto);

			if (actualizado) {
				return ResponseEntity.ok().body("{\"mensaje\": \"✅ Usuario actualizado correctamente.\"}");
			} else {
				return ResponseEntity.status(400).body("{\"mensaje\": \"❌ No se pudo actualizar el usuario.\"}");
			}
		} catch (Exception e) {
			logger.error("❌ Error al actualizar usuario:", e);
			return ResponseEntity.status(500).body("{\"mensaje\": \"❌ Error interno del servidor.\"}");
		}
	}


	/**
	 * Determina la página a la que debe ser redirigido un usuario según su rol.*
	 * @param session Objeto `HttpSession` para obtener la información del usuario en sesión.
	 * @return `String` con la redirección a la vista correspondiente (perfil de usuario o panel de administración).
	 */


	@GetMapping("/usuario/perfil")
	public String perfilUsuario(HttpSession session) {
		UsuarioDto usuario = (UsuarioDto) session.getAttribute("usuario");

		if (usuario == null) {
			return "redirect:/login"; // Si no hay sesión, redirigir al login
		}

		if ("ROLE_ADMIN".equals(usuario.getRol())) {
			return "redirect:/admin/panel"; // Si es admin, redirigir al panel de administración
		}

		// Si es usuario normal, ir al perfil del usuario
		return "perfil-usuario";
	}

	@PostMapping("/reenviar-verificacion")
	@ResponseBody
	public ResponseEntity<String> reenviarVerificacion(@RequestParam String email) {
		try {
			String respuesta = usuarioService.reenviarCorreoVerificacion(email);
			return ResponseEntity.ok(respuesta);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error al reenviar el correo.");
		}
	}

	@GetMapping("/detalle")
	public ResponseEntity<?> obtenerDetalleUsuario(@RequestParam String email) {
		try {
			logger.info("🔍 Solicitando detalle del usuario con email: {}", email);
			UsuarioDto usuario = usuarioService.obtenerDetalleUsuarioDesdeAPI(email);
			if (usuario != null) {
				return ResponseEntity.ok(usuario);
			} else {
				return ResponseEntity.status(404).body("Usuario no encontrado");
			}
		} catch (Exception e) {
			logger.error("❌ Error al obtener detalle del usuario: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Error en el servidor");
		}
	}



}