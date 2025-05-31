package com.tfg.terranostra.services;

import com.tfg.terranostra.dto.UsuarioDto;
import com.tfg.terranostra.models.CorreoVerificacionToken;
import com.tfg.terranostra.models.PasswordResetToken;
import com.tfg.terranostra.models.UsuarioModel;
import com.tfg.terranostra.repositories.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ReseniaRepository reseniaRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorreoService correoService;

    @Autowired
    private CorreoVerificacionTokenRepository correoVerificacionTokenRepository;


    @Transactional
    public UsuarioModel aniadirUsuario(@Valid UsuarioDto userDto) {
        logger.info("🟢 Intentando registrar un nuevo usuario con email: {}", userDto.getEmail());

        if (usuarioRepository.findByEmail(userDto.getEmail()).isPresent()) {
            logger.warn("⚠️ Intento fallido: el email {} ya está registrado", userDto.getEmail());
            throw new RuntimeException("❌ El email ya está registrado");
        }

        UsuarioModel user = new UsuarioModel();
        user.setNombre(userDto.getNombre());
        user.setApellido(userDto.getApellido());
        user.setContrasenia(passwordEncoder.encode(userDto.getContrasenia()));
        user.setEmail(userDto.getEmail());
        user.setDireccion(userDto.getDireccion());
        user.setTelefono(userDto.getTelefono());
        user.setFechaRegistro(userDto.getFechaRegistro());
        user.setCorreoVerificado(false);
        user.setRol("ROLE_USER");

        UsuarioModel savedUser = usuarioRepository.save(user);
        logger.info("✅ Usuario registrado con éxito: {}", savedUser.getEmail());

        try {
            enviarEnlaceVerificacionCuenta(savedUser.getEmail());
        } catch (Exception e) {
            logger.error("❌ Error al enviar correo de verificación: {}", e.getMessage());
        }

        return savedUser;
    }


    @Transactional(readOnly = true)
    public List<UsuarioDto> obtenerTodos() {
        logger.info("📋 Obteniendo la lista de todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioDto(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getEmail(),
                        usuario.getContrasenia(),
                        usuario.getTelefono(),
                        usuario.getDireccion(),
                        usuario.getCorreoVerificado(),
                        usuario.getFechaRegistro(),
                        usuario.getFechaModificacion(),
                        usuario.getRol()
                ))
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public UsuarioDto obtenerPorEmail(String email) {
        logger.info("🔍 Buscando usuario por email: {}", email);

        return usuarioRepository.findByEmail(email)
                .map(usuario -> new UsuarioDto(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getEmail(),
                        usuario.getContrasenia(),
                        usuario.getTelefono(),
                        usuario.getDireccion(),
                        usuario.getCorreoVerificado(),
                        usuario.getFechaRegistro(),
                        usuario.getFechaModificacion(),
                        usuario.getRol()
                ))
                .orElse(null);
    }


    @Transactional
    public boolean actualizarUsuarioPorEmail(String email, UsuarioDto userDto) {
        logger.info("🛠️ Intentando actualizar usuario con email: {}", email);

        return usuarioRepository.findByEmail(email).map(usuario -> {
            logger.debug("📥 Datos actuales del usuario antes de la actualización: {}", usuario);
            logger.debug("📤 Datos recibidos para actualización: {}", userDto);

            if (userDto.getId() != null) {
                usuario.setId(userDto.getId());
            }

            usuario.setNombre(userDto.getNombre());
            usuario.setApellido(userDto.getApellido());
            usuario.setTelefono(userDto.getTelefono());
            usuario.setDireccion(userDto.getDireccion());
            usuario.setRol(userDto.getRol());
            usuario.setFechaModificacion(LocalDateTime.now());

            if (userDto.getFechaRegistro() != null) {
                usuario.setFechaRegistro(userDto.getFechaRegistro());
            }

            usuarioRepository.save(usuario);
            logger.info("✅ Usuario con email {} actualizado correctamente", email);
            return true;
        }).orElseGet(() -> {
            logger.warn("⚠️ No se encontró usuario con email: {} para actualizar", email);
            return false;
        });
    }

    @Transactional
    public boolean eliminarUsuarioPorEmail(String email) {
        logger.info("🗑️ Intentando eliminar usuario con email: {}", email);

        return usuarioRepository.findByEmail(email).map(usuario -> {
            // Verifica si es ADMIN
            String rolUsuario = usuario.getRol() != null ? usuario.getRol().trim().toUpperCase() : "";

            if ("ROLE_ADMIN".equals(rolUsuario)) {
                long totalAdmins = usuarioRepository.findAll().stream()
                        .filter(u -> "ROLE_ADMIN".equalsIgnoreCase(
                                u.getRol() != null ? u.getRol().trim() : ""))
                        .count();

                if (totalAdmins <= 1) {
                    logger.warn("⛔ No se puede eliminar al último usuario ROLE_ADMIN: {}", email);
                    throw new IllegalStateException("No se puede eliminar al último usuario ADMIN.");
                }
            }


            Long usuarioId = usuario.getId();

            // 🧹 Eliminar datos relacionados
            carritoRepository.deleteByUsuarioId(usuarioId);
            pedidoRepository.deleteByUsuarioId(usuarioId);
            reseniaRepository.deleteByUsuarioId(usuarioId);
            // Aquí puedes agregar más entidades si las tienes

            // 🔥 Eliminar el usuario
            usuarioRepository.delete(usuario);
            logger.info("✅ Usuario con email {} y datos relacionados eliminados correctamente", email);
            return true;

        }).orElseGet(() -> {
            logger.warn("⚠️ No se encontró usuario con email: {} para eliminar", email);
            return false;
        });
    }



    /**
     * Envia un enlace de recuperación de contraseña al correo del usuario.
     * Genera y guarda un token temporal en la base de datos.
     */
    @Transactional
    public void enviarEnlaceCambioContrasenia(String email) throws Exception {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("No se encontró usuario con ese email"));

        // Buscar y eliminar token existente si lo hay
        Optional<PasswordResetToken> tokenExistente = tokenRepository.findByEmail(email);
        tokenExistente.ifPresent(tokenRepository::delete);

        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(10);

        PasswordResetToken nuevoToken = new PasswordResetToken();
        nuevoToken.setEmail(email);
        nuevoToken.setToken(token);
        nuevoToken.setExpiracion(expiracion);

        tokenRepository.save(nuevoToken);

        // Generar enlace de recuperación
        String link = "http://localhost:8080/cambiar-contrasenia?token=" + token;

        String asunto = "🔐 Recuperación de contraseña - Terra Nostra";
        String cuerpoHtml = """
        <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
            <h2 style="color: #F18817;">🔐 Recuperación de contraseña</h2>
            <p>Hola <strong>%s</strong>,</p>
            <p>Haz clic en el siguiente botón para cambiar tu contraseña:</p>
            <p style="text-align: center;">
                <a href="%s" style="display: inline-block; padding: 12px 24px;
                   background-color: #F18817; color: white; text-decoration: none;
                   border-radius: 5px; font-weight: bold;">
                   Cambiar contraseña
                </a>
            </p>
            <p>Este enlace expirará en 10 minutos.</p>
            <hr style="margin-top: 30px;">
            <p style="font-size: 12px; color: #888;">
                © 2025 Terra Nostra · No responder a este correo automático.
            </p>
        </div>
        """.formatted(usuario.getNombre(), link);

        correoService.enviarCorreo(email, asunto, cuerpoHtml);
    }

    @Transactional
    public void enviarEnlaceVerificacionCuenta(String email) throws Exception {
        logger.info("📨 Iniciando proceso de envío de verificación para: {}", email);

        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("No se encontró usuario con ese email: " + email));

        if (usuario.getCorreoVerificado()) {
            logger.info("ℹ️ El usuario {} ya tiene el correo verificado. No se enviará nada.", email);
            return;
        }

        correoVerificacionTokenRepository.findByEmail(email)
                .ifPresent(token -> {
                    logger.info("♻️ Eliminando token anterior para: {}", email);
                    correoVerificacionTokenRepository.delete(token);
                });

        String token = null;
        int intentos = 0;
        final int maxIntentos = 3;
        boolean guardado = false;

        while (!guardado && intentos < maxIntentos) {
            token = UUID.randomUUID().toString();
            CorreoVerificacionToken nuevoToken = new CorreoVerificacionToken();
            nuevoToken.setEmail(email);
            nuevoToken.setToken(token);
            nuevoToken.setExpiracion(LocalDateTime.now().plusMinutes(30));

            try {
                correoVerificacionTokenRepository.save(nuevoToken);
                guardado = true;
                logger.info("✅ Token generado y guardado: {}", token);
            } catch (Exception e) {
                intentos++;
                logger.warn("⚠️ Error al guardar token (intento {}). Probablemente duplicado. Regenerando...", intentos);
                if (intentos == maxIntentos) {
                    logger.error("❌ No se pudo generar un token único tras {} intentos", maxIntentos);
                    throw new Exception("No se pudo generar un token único para verificación.");
                }
            }
        }

        String link = "http://localhost:8081/verificacion/confirmar?token=" + token;
        String asunto = "📩 Verifica tu cuenta en Terra Nostra";

        String cuerpoHtml = """
    <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
        <h2 style="color: #F18817;">📩 Verificación de correo</h2>
        <p>Hola <strong>%s</strong>,</p>
        <p>Gracias por registrarte en Terra Nostra. Haz clic en el siguiente botón para verificar tu cuenta:</p>
        <p style="text-align: center;">
            <a href="%s" style="display: inline-block; padding: 12px 24px;
               background-color: #28a745; color: white; text-decoration: none;
               border-radius: 5px; font-weight: bold;">
               Verificar cuenta
            </a>
        </p>
        <p>Este enlace expirará en 30 minutos.</p>
        <hr style="margin-top: 30px;">
        <p style="font-size: 12px; color: #888;">
            © 2025 Terra Nostra · No responder a este correo automático.
        </p>
    </div>
    """.formatted(usuario.getNombre(), link);

        try {
            correoService.enviarCorreo(email, asunto, cuerpoHtml);
            logger.info("✉️ Correo de verificación enviado correctamente a {}", email);
        } catch (Exception e) {
            logger.error("❌ Error al enviar el correo a {}: {}", email, e.getMessage(), e);
            throw new Exception("Error al enviar el correo de verificación", e);
        }
    }

}
