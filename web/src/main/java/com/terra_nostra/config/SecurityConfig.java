package com.terra_nostra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

/**
 * Configuración de seguridad de Spring Security.
 * Define una cadena de filtros de seguridad (`SecurityFilterChain`) con reglas de acceso personalizadas.
 *
 * @author ebp
 * @version 1.0
 */
public class SecurityConfig {

    /**
     * Configura la seguridad de la aplicación.
     *
     * - Permite el acceso a todas las rutas sin autenticación (`permitAll()`).
     * - Deshabilita la protección CSRF (`csrf.disable()`), útil en APIs sin sesiones.
     * - Deshabilita el formulario de inicio de sesión (`formLogin.disable()`).
     * - Deshabilita la funcionalidad de logout (`logout.disable()`).
     * - Habilita CORS para permitir llamadas desde el frontend (por ejemplo, localhost:8080).
     *
     * @param http Objeto `HttpSecurity` para configurar las reglas de seguridad.
     * @return Una instancia de `SecurityFilterChain` con la configuración definida.
     * @throws Exception Si ocurre algún error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitir acceso a todas las rutas
                )
                .cors(Customizer.withDefaults()) // Habilitar CORS con configuración por defecto
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF si es necesario
                .formLogin(form -> form.disable()) // Deshabilitar el formulario de login
                .logout(logout -> logout.disable()); // Deshabilitar logout

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080"); // Permite peticiones desde el frontend
        configuration.addAllowedMethod("*"); // Permite todos los métodos: GET, POST, etc.
        configuration.addAllowedHeader("*"); // Permite todos los headers
        configuration.setAllowCredentials(true); // Permite enviar cookies y credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    /**
     * Configuración global de CORS.
     * Permite peticiones desde el frontend (por ejemplo, desde http://localhost:8080).
     *
     * @return Configurador de CORS con reglas personalizadas.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
