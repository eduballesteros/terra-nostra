package com.terra_nostra.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.terra_nostra.dto.PedidoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    private static final String API_URL = "http://terraapi:8080/api/pedidos/usuario/";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public PedidoService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<PedidoDto> obtenerPedidosPorUsuario(Long usuarioId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + usuarioId))
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                logger.error("❌ Error HTTP {} al obtener pedidos: {}", response.statusCode(), response.body());
                throw new RuntimeException("Error al obtener los pedidos");
            }

            return mapper.readValue(response.body(), new TypeReference<List<PedidoDto>>() {});

        } catch (Exception e) {
            logger.error("❌ Excepción al obtener pedidos del usuario {}: {}", usuarioId, e.getMessage());
            throw new RuntimeException("Error al obtener los pedidos", e);
        }
    }
}
