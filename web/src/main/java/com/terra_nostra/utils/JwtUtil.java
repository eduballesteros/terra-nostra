    package com.terra_nostra.utils;

    import io.jsonwebtoken.*;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;

    import javax.crypto.SecretKey;
    import java.util.Base64;
    import java.util.Date;

    @Component
    public class JwtUtil {

        private final SecretKey secretKey;

        public JwtUtil(@Value("${jwt.secret}") String secret) {
            byte[] decodedKey = Base64.getDecoder().decode(secret); // Decodificamos Base64
            this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        }

        public String generarToken(String email, String rol, String nombre, boolean verificado, Long id) {
            Date now = new Date();
            Date expiryDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10); // 10 horas

            return Jwts.builder()
                    .setSubject(email)
                    .claim("rol", rol)
                    .claim("nombre", nombre)
                    .claim("verificado", verificado)
                    .claim("usuarioId", id)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        }


        public String obtenerRolDesdeToken(String token) {
            return parseClaims(token).get("rol", String.class);
        }

        public Long obtenerUsuarioIdDesdeToken(String token) {
            return parseClaims(token).get("usuarioId", Long.class);
        }

        public boolean obtenerVerificacionCorreoDesdeToken(String token) {
            return Boolean.TRUE.equals(parseClaims(token).get("verificado", Boolean.class));
        }


        public String obtenerNombreDesdeToken(String token) {
            return parseClaims(token).get("nombre", String.class);
        }

        public String obtenerEmailDesdeToken(String token) {
            return parseClaims(token).getSubject();
        }

        public boolean isTokenValido(String token) {
            try {
                Claims claims = parseClaims(token);

                Date expirationDate = claims.getExpiration();
                return expirationDate.after(new Date());
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        }

        private Claims parseClaims(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }
