package com.tfg.terranostra.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración de seguridad para la aplicación.
 * Configura la autenticación y las políticas de seguridad.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean para gestionar la autenticación.
     *
     * @param authConfig Configuración de autenticación proporcionada por Spring Security.
     * @return AuthenticationManager para gestionar la autenticación de usuarios.
     * @throws Exception Si ocurre un error al obtener el AuthenticationManager.
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configuración de seguridad de la aplicación.

     * - Deshabilita CSRF (Cross-Site Request Forgery) ya que la API es stateless.<br>
     * - Permite el acceso sin autenticación a los endpoints de registro y login.<br>
     * - Configura la política de sesión como STATELESS, adecuada para JWT o APIs sin estado.<br>
     * - Deshabilita el formulario de login y la autenticación básica.
     * *
     * @param http Configuración de seguridad HTTP.
     * @return SecurityFilterChain con la configuración aplicada.
     * @throws Exception Si ocurre un error al construir la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Deshabilitar CSRF si usas Postman o una API sin cookies
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/usuario/registro", "/api/auth/login").permitAll() // Endpoints públicos
                        .anyRequest().permitAll() // Permitir cualquier otra solicitud sin autenticación
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin estado
                .formLogin(form -> form.disable())  // Deshabilitar formularios de login
                .httpBasic(httpBasic -> httpBasic.disable());  // Deshabilitar autenticación básica

        return http.build();
    }

    /**
     * Bean para codificar contraseñas con BCrypt.
     *
     * @return PasswordEncoder basado en BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
