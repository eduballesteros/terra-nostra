package com.tfg.terranostra.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.terranostra.dto.CambioContraseniaDto;
import com.tfg.terranostra.dto.LoginDto;
import com.tfg.terranostra.dto.TokenGoogleDto;
import com.tfg.terranostra.dto.UsuarioDto;
import com.tfg.terranostra.models.PasswordResetToken;
import com.tfg.terranostra.models.UsuarioModel;
import com.tfg.terranostra.repositories.PasswordResetTokenRepository;
import com.tfg.terranostra.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioDto autenticarUsuario(LoginDto loginDto) {
        logger.info("📩 Intentando autenticar usuario con email: {}", loginDto.getEmail());

        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByEmail(loginDto.getEmail());

        if (usuarioOpt.isEmpty()) {
            logger.warn("❌ Usuario no encontrado en la base de datos: {}", loginDto.getEmail());
            return null;
        }

        UsuarioModel usuarioModel = usuarioOpt.get();
        boolean match = passwordEncoder.matches(loginDto.getContrasenia(), usuarioModel.getContrasenia());
        logger.info("🔎 ¿Las contraseñas coinciden? {}", match);

        if (!match) {
            logger.error("❌ Contraseña incorrecta para el usuario: {}", loginDto.getEmail());
            return null;
        }

        // ⚠️ Comprobación segura del correo verificado
        if (!Boolean.TRUE.equals(usuarioModel.getCorreoVerificado())) {
            logger.warn("⚠️ Usuario no tiene correo verificado: {}", loginDto.getEmail());
            return null;
        }

        UsuarioDto usuarioDto = new UsuarioDto(
                usuarioModel.getId(),
                usuarioModel.getNombre(),
                usuarioModel.getApellido(),
                usuarioModel.getEmail(),
                usuarioModel.getContrasenia(),
                usuarioModel.getTelefono(),
                usuarioModel.getDireccion(),
                usuarioModel.getCorreoVerificado(),
                usuarioModel.getFechaRegistro(),
                usuarioModel.getFechaModificacion(),
                usuarioModel.getRol()
        );

        UserDetails userDetails = User.withUsername(usuarioModel.getEmail())
                .password(usuarioModel.getContrasenia())
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("🔐 Usuario autenticado correctamente en SecurityContext: {}", usuarioModel.getEmail());

        return usuarioDto;
    }

    @Transactional
    public boolean cambiarContrasenia(CambioContraseniaDto dto) {
        logger.info("🔁 Intentando cambiar la contraseña con token: {}", dto.getToken());

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(dto.getToken());

        if (tokenOpt.isEmpty()) {
            logger.warn("❌ Token no encontrado: {}", dto.getToken());
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.getExpiracion().isBefore(LocalDateTime.now())) {
            logger.warn("❌ Token expirado: {}", dto.getToken());
            return false;
        }

        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findByEmail(token.getEmail());

        if (usuarioOpt.isEmpty()) {
            logger.error("❌ No existe un usuario asociado al email: {}", token.getEmail());
            return false;
        }

        UsuarioModel usuario = usuarioOpt.get();
        usuario.setContrasenia(passwordEncoder.encode(dto.getNuevaContrasenia()));
        usuario.setFechaModificacion(LocalDateTime.now());

        usuarioRepository.save(usuario);
        tokenRepository.delete(token);

        logger.info("✅ Contraseña actualizada exitosamente para el usuario: {}", usuario.getEmail());
        return true;
    }

    public ResponseEntity<?> loginConGoogle(TokenGoogleDto tokenGoogleDto) {
        logger.info("🔵 Recibiendo token de Google para login");

        try {
            String idToken = tokenGoogleDto.getIdToken();

            // Validar el token con Google
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            String result = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(result);

            if (root.has("error_description")) {
                logger.error("❌ Error validando token de Google: {}", root.get("error_description").asText());
                return ResponseEntity.badRequest().body("Token de Google inválido.");
            }

            String email = root.path("email").asText();
            String nombre = root.path("given_name").asText();
            String apellido = root.path("family_name").asText();

            logger.info("📨 Token válido. Email extraído: {}", email);

            // Buscar si ya existe el usuario
            Optional<UsuarioModel> usuarioExistente = usuarioRepository.findByEmail(email);

            UsuarioModel usuario;
            if (usuarioExistente.isPresent()) {
                usuario = usuarioExistente.get();
                logger.info("✅ Usuario ya registrado. Procediendo al login: {}", email);
            } else {
                usuario = new UsuarioModel();
                usuario.setNombre(nombre);
                usuario.setApellido(apellido);
                usuario.setEmail(email);
                usuario.setContrasenia(passwordEncoder.encode("login_google"));
                usuario.setRol("USUARIO");
                usuario.setCorreoVerificado(true); // Google login = ya verificado
                usuario.setFechaRegistro(LocalDateTime.now());
                usuario.setFechaModificacion(LocalDateTime.now());
                usuarioRepository.save(usuario);

                logger.info("🆕 Nuevo usuario registrado por Google: {}", email);
            }

            UserDetails userDetails = User.withUsername(usuario.getEmail())
                    .password(usuario.getContrasenia())
                    .roles(usuario.getRol())
                    .build();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("🔐 Usuario autenticado correctamente en SecurityContext: {}", usuario.getEmail());

            UsuarioDto usuarioDto = new UsuarioDto(
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
            );

            return ResponseEntity.ok(usuarioDto);

        } catch (Exception e) {
            logger.error("❌ Error procesando login con Google", e);
            return ResponseEntity.status(500).body("Error interno en el login con Google: " + e.getMessage());
        }
    }
}
