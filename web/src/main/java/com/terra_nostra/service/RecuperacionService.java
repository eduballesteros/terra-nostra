package com.terra_nostra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terra_nostra.dto.CambioContraseniaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

@Service
public class RecuperacionService {

    private static final Logger logger = LoggerFactory.getLogger(RecuperacionService.class);
    private static final String API_BASE_URL = "http://terraapi:8080/api/auth"; // la URL base de tu API

    public boolean cambiarContrasenia(String token, String nuevaContrasenia) {
        try {
            HttpClient cliente = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            CambioContraseniaDto contrasenia = new CambioContraseniaDto();
            contrasenia.setToken(token);
            contrasenia.setNuevaContrasenia(nuevaContrasenia);

            String json = mapper.writeValueAsString(contrasenia);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_BASE_URL + "/cambiar-password"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("🔁 Respuesta del cambio de contraseña: {}", response.body());
            return response.statusCode() == 200;

        } catch (Exception e) {
            logger.error("❌ Error al cambiar la contraseña", e);
            return false;
        }
    }

    public boolean solicitarCambioContrasenia(String email) {
        try {
            HttpClient cliente = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            // Cuerpo JSON con el email
            String json = mapper.writeValueAsString(Collections.singletonMap("email", email));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/api/usuario/solicitar-cambio")) //
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("📬 Respuesta del envío de enlace: {}", response.body());

            return response.statusCode() == 200;

        } catch (Exception e) {
            logger.error("❌ Error al solicitar recuperación de contraseña para: " + email, e);
            return false;
        }
    }
}
