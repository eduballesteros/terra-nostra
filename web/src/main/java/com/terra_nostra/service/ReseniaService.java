package com.terra_nostra.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.terra_nostra.dto.CrearReseniaDto;
import com.terra_nostra.dto.ReseniaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.util.Collections;
import java.util.List;

/**
 * Servicio para la gestión de resenias desde el cliente web hacia la API REST.
 */
@Service
public class ReseniaService {

    private static final Logger logger = LoggerFactory.getLogger(ReseniaService.class);
    private static final String API_URL = "http://terraapi:8080/api/resenias";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public ReseniaService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Envía una nueva resenia a la API.
     *
     * @param dto Objeto con los datos de la resenia a crear.
     * @return true si se creó correctamente, false si hubo error.
     */
    public ReseniaDto crearResenia(CrearReseniaDto dto) {
        try {
            if (dto.getProductoId() == null || dto.getUsuarioId() == null ||
                    dto.getComentario() == null || dto.getComentario().trim().isEmpty() ||
                    dto.getValoracion() < 1 || dto.getValoracion() > 5) {
                throw new IllegalArgumentException("Faltan campos válidos en la reseña.");
            }

            logger.info("📤 Enviando reseña a la API: {}", dto);
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("🔹 Código de respuesta: {}", response.statusCode());
            logger.info("🔹 Cuerpo: {}", response.body());

            if (response.statusCode() == 201) {
                return mapper.readValue(response.body(), ReseniaDto.class);
            } else {
                throw new RuntimeException("Error al crear reseña: " + response.body());
            }

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Reseña inválida: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error al enviar reseña a la API", e);
            throw new RuntimeException("Error inesperado al crear reseña", e);
        }
    }


    /**
     * Obtiene todas las resenias de un producto desde la API.
     *
     * @param productoId ID del producto.
     * @return Lista de ReseniaDto o vacía en caso de error.
     */
    public List<ReseniaDto> listarReseniasPorProducto(Long productoId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/producto/" + productoId))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<ReseniaDto> lista = mapper.readValue(response.body(), new TypeReference<List<ReseniaDto>>() {});
                logger.info("✅ {} resenias cargadas para producto ID {}", lista.size(), productoId);
                return lista;
            } else {
                logger.warn("⚠️ No se pudieron obtener resenias. Código: {}, Respuesta: {}", response.statusCode(), response.body());
                return Collections.emptyList();
            }

        } catch (Exception e) {
            logger.error("❌ Error al obtener resenias desde la API", e);
            return Collections.emptyList();
        }
    }
}
