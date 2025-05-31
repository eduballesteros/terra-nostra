package com.terra_nostra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.terra_nostra.dto.CarritoDto;
import com.terra_nostra.dto.CarritoItemDto;
import com.terra_nostra.dto.CrearPedidoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class CarritoService {

    private static final Logger logger = LoggerFactory.getLogger(CarritoService.class);
    private static final String API_BASE_URL = "http://terraapi:8080/carrito";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public CarritoService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public CarritoDto obtenerCarrito(Long usuarioId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + usuarioId))
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                logger.error("❌ Error HTTP {} al obtener carrito: {}", response.statusCode(), response.body());
                throw new RuntimeException("Error al obtener carrito");
            }

            return mapper.readValue(response.body(), CarritoDto.class);

        } catch (Exception e) {
            logger.error("❌ Excepción al obtener el carrito del usuario {}: {}", usuarioId, e.getMessage());
            throw new RuntimeException("Error al obtener el carrito", e);
        }
    }

    public void agregarProducto(Long usuarioId, CarritoItemDto item) {
        try {
            String json = mapper.writeValueAsString(item);
            System.out.println("🧪 JSON generado desde CarritoItemDto:\n" + json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/agregar?usuarioId=" + usuarioId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                logger.error("❌ Error al agregar producto: {}", response.body());
                throw new RuntimeException("Error al agregar producto al carrito");
            }

        } catch (Exception e) {
            logger.error("❌ Excepción al agregar producto al carrito: {}", e.getMessage());
            throw new RuntimeException("Error al agregar producto al carrito", e);
        }
    }

    public void vaciarCarrito(Long usuarioId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + usuarioId + "/vaciar"))
                    .DELETE()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                logger.error("❌ Error al vaciar carrito: {}", response.body());
                throw new RuntimeException("Error al vaciar carrito");
            }

        } catch (Exception e) {
            logger.error("❌ Excepción al vaciar carrito del usuario {}: {}", usuarioId, e.getMessage());
            throw new RuntimeException("Error al vaciar carrito", e);
        }
    }

    public void finalizarCompra(CrearPedidoDto dto) {
        try {
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/finalizar"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                logger.error("❌ Error al finalizar compra: {}", response.body());
                throw new RuntimeException("Error al finalizar la compra");
            }

        } catch (Exception e) {
            logger.error("❌ Excepción al finalizar la compra: {}", e.getMessage());
            throw new RuntimeException("Error al finalizar la compra", e);
        }
    }

    /**
     * Elimina completamente un producto del carrito remoto.
     */
    public void eliminarProducto(Long usuarioId, Long productoId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + usuarioId + "/producto/" + productoId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                logger.error("❌ Error HTTP {} al eliminar producto {} del carrito {}: {}",
                        response.statusCode(), productoId, usuarioId, response.body());
                throw new RuntimeException("Error al eliminar producto del carrito");
            }
        } catch (Exception e) {
            logger.error("❌ Excepción al eliminar producto {} del carrito {}: {}",
                    productoId, usuarioId, e.getMessage());
            throw new RuntimeException("Error al eliminar producto del carrito", e);
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito remoto.
     */
    public void actualizarCantidad(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        try {
            // Preparamos el JSON { "cantidad": nuevaCantidad }
            String json = mapper.writeValueAsString(Map.of("cantidad", nuevaCantidad));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + usuarioId + "/producto/" + productoId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                logger.error("❌ Error HTTP {} al actualizar cantidad de producto {} en carrito {}: {}",
                        response.statusCode(), productoId, usuarioId, response.body());
                throw new RuntimeException("Error al actualizar cantidad de producto");
            }
        } catch (Exception e) {
            logger.error("❌ Excepción al actualizar cantidad de producto {} en carrito {}: {}",
                    productoId, usuarioId, e.getMessage());
            throw new RuntimeException("Error al actualizar cantidad de producto", e);
        }
    }

    public void procesarPedidoTrasPago(Long usuarioId, Map<String, String> envio) {
        try {
            // 1. Obtener carrito actual del usuario desde la API
            CarritoDto carrito = obtenerCarrito(usuarioId);

            // 2. Construir lista de productos con precio y cantidad
            var productos = carrito.getItems().stream().map(item -> {
                var p = new com.terra_nostra.dto.ProductoPedidoDto();
                p.setProductoId(item.getProductoId());
                p.setCantidad(item.getCantidad());
                p.setPrecioUnitario(item.getPrecioUnitario());
                p.setNombre(item.getNombre());
                return p;
            }).toList();


            // 3. Construir el objeto CrearPedidoDto
            CrearPedidoDto pedido = new CrearPedidoDto();
            pedido.setUsuarioId(usuarioId);
            pedido.setEmailUsuario(envio.get("email"));
            pedido.setMetodoPago("paypal");
            pedido.setDireccionEnvio(envio.get("direccion") + ", " + envio.get("postal") + " " + envio.get("ciudad"));
            pedido.setTelefonoContacto("no-aplica"); // o pedirlo en el formulario
            pedido.setProductos(productos);

            // 4. Enviar pedido a la API y vaciar el carrito
            finalizarCompra(pedido);
            vaciarCarrito(usuarioId);

            logger.info("✅ Pedido finalizado y carrito vaciado para usuario {}", usuarioId);
        } catch (Exception e) {
            logger.error("❌ Error al procesar el pedido tras pago para usuario {}: {}", usuarioId, e.getMessage());
            throw new RuntimeException("Error al procesar el pedido tras el pago", e);
        }
    }


}
