    package com.terra_nostra.service;

    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.SerializationFeature;
    import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
    import com.terra_nostra.dto.ProductoDto;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.net.URI;
    import java.net.URLEncoder;
    import java.net.http.HttpClient;
    import java.net.http.HttpRequest;
    import java.net.http.HttpResponse;
    import java.nio.charset.StandardCharsets;
    import java.util.Collections;
    import java.util.List;

    @Service

    /**
     * Servicio para la gestión de productos.
     * Proporciona métodos para interactuar con la API externa de productos.*
     * @author ebp
     * @version 1.0
     */


    public class ProductoService {

        private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);
        private static final String API_BASE_URL = "http://terraapi:8080/api/productos";

        private final HttpClient cliente = HttpClient.newHttpClient();
        private final ObjectMapper mapeo;

        public ProductoService() {
            this.mapeo = new ObjectMapper();
            this.mapeo.registerModule(new JavaTimeModule());
            this.mapeo.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        /**
         * Guarda un nuevo producto en la API.
         * @param productoDto DTO que contiene la información del producto a guardar.
         * @param imagen Imagen opcional del producto en formato `MultipartFile`.
         * @return `ProductoDto` con los datos del producto guardado si la operación fue exitosa,
         *         o `null` si hubo un error.
         */


        public ProductoDto guardarProducto(ProductoDto productoDto, MultipartFile imagen) {
            try {
                if (imagen != null && !imagen.isEmpty()) {
                    productoDto.setImagen(imagen.getBytes());
                    logger.info("📸 Imagen recibida, tamaño: {} bytes", productoDto.getImagen().length);
                }

                String requestBody = mapeo.writeValueAsString(productoDto);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/guardar"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    // Devuelve el producto guardado desde la API
                    return mapeo.readValue(response.body(), ProductoDto.class);
                } else {
                    logger.error("❌ Error al guardar producto: {}", response.body());
                    return null;
                }
            } catch (Exception e) {
                logger.error("❌ Excepción al enviar producto a la API: ", e);
                return null;
            }
        }

        /**
         * Obtiene la lista de todos los productos disponibles en la API.
         * @return Lista de `ProductoDto` con los productos obtenidos,
         *         o una lista vacía si no se encontraron productos o hubo un error.
         */


        public List<ProductoDto> obtenerTodosLosProductos() {
            try {
                logger.info("🔍 Enviando solicitud GET a la API para obtener productos...");

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/listar"))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                logger.info("🔹 Código de respuesta de la API: {}", response.statusCode());

                if (response.statusCode() == 200) {
                    return mapeo.readValue(response.body(), new TypeReference<List<ProductoDto>>() {});
                } else {
                    logger.error("❌ Error al obtener productos: {}", response.body());
                    return Collections.emptyList();
                }

            } catch (Exception e) {
                logger.error("❌ Excepción al obtener productos de la API: ", e);
                return Collections.emptyList();
            }
        }

        /**
         * Actualiza un producto en la API con los nuevos datos proporcionados.
         * @param id Identificador del producto a actualizar.
         * @param productoDto DTO con los datos actualizados del producto.
         * @param imagen Nueva imagen opcional del producto en formato `MultipartFile`.
         * @return `success` si el producto fue actualizado correctamente, o `error` si hubo un problema.
         */


        public String editarProducto(Long id, ProductoDto productoDto, MultipartFile imagen) {
            try {
                if (imagen != null && !imagen.isEmpty()) {
                    productoDto.setImagen(imagen.getBytes());
                    logger.info("📸 Nueva imagen recibida y convertida a byte[]");
                }

                String requestBody = mapeo.writeValueAsString(productoDto);
                logger.info("📤 Enviando JSON a la API para actualizar el producto: {}", requestBody);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/editar/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                logger.info("🔹 Código de respuesta de la API: {}", response.statusCode());

                if (response.statusCode() == 200) {
                    logger.info("✅ Producto actualizado correctamente.");
                    return "success";
                } else {
                    logger.error("❌ Error al actualizar producto: {}", response.body());
                    return "error";
                }

            } catch (Exception e) {
                logger.error("❌ Excepción al actualizar el producto: ", e);
                return "error";
            }
        }

        /**
         * Obtiene los detalles de un producto específico a partir de su ID.
         * @param id Identificador del producto a obtener.
         * @return `ProductoDto` con la información del producto si se encuentra,
         *         o `null` si no se encuentra el producto.
         */


        public ProductoDto obtenerProductoPorId(Long id) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/obtener/" + id))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return mapeo.readValue(response.body(), ProductoDto.class);
                } else {
                    logger.warn("⚠ Producto no encontrado. ID: {}", id);
                    return null;
                }

            } catch (Exception e) {
                logger.error("❌ Error al obtener producto de la API: ", e);
                throw new RuntimeException("Producto no encontrado");
            }
        }

        /**
         * Elimina un producto de la API utilizando su ID.
         * @param id Identificador del producto a eliminar.
         * @return `success` si el producto fue eliminado correctamente, o `error` si hubo un problema.
         */


        public String eliminarProducto(Long id) {
            try {
                logger.info("🗑 Enviando solicitud DELETE para eliminar producto ID: {}", id);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/eliminar/" + id))
                        .header("Accept", "application/json")
                        .DELETE()
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                logger.info("🔹 Código de respuesta de la API: {}", response.statusCode());

                if (response.statusCode() == 200) {
                    logger.info("✅ Producto eliminado correctamente.");
                    return "success";
                } else {
                    logger.error("❌ Error al eliminar producto: {}", response.body());
                    return "error";
                }

            } catch (Exception e) {
                logger.error("❌ Excepción al eliminar el producto: ", e);
                return "error";
            }
        }

        public List<String> obtenerCategorias() {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/categorias"))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return mapeo.readValue(response.body(), new TypeReference<List<String>>() {});
                } else {
                    logger.error("❌ Error al obtener categorías: {}", response.body());
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                logger.error("❌ Excepción al obtener categorías de la API:", e);
                return Collections.emptyList();
            }
        }


        public List<ProductoDto> buscarProductosConFiltros(String texto, Boolean disponibles, String categoria) {
            try {
                StringBuilder urlBuilder = new StringBuilder(API_BASE_URL + "/listar");

                boolean tieneFiltro = texto != null || disponibles != null || categoria != null;
                if (tieneFiltro) {
                    urlBuilder.append("?");
                    if (texto != null && !texto.trim().isEmpty()) {
                        urlBuilder.append("texto=").append(URLEncoder.encode(texto, StandardCharsets.UTF_8)).append("&");
                    }
                    if (disponibles != null) {
                        urlBuilder.append("disponibles=").append(disponibles).append("&");
                    }
                    if (categoria != null && !categoria.trim().isEmpty()) {
                        urlBuilder.append("categoria=").append(URLEncoder.encode(categoria, StandardCharsets.UTF_8)).append("&");
                    }
                    // Elimina el último "&" si lo hay
                    if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
                        urlBuilder.setLength(urlBuilder.length() - 1);
                    }
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(urlBuilder.toString()))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return mapeo.readValue(response.body(), new TypeReference<List<ProductoDto>>() {});
                } else {
                    logger.error("❌ Error al obtener productos con filtros: {}", response.body());
                    return Collections.emptyList();
                }

            } catch (Exception e) {
                logger.error("❌ Excepción al filtrar productos desde la API: ", e);
                return Collections.emptyList();
            }
        }

        public ProductoDto obtenerProductoPorNombre(String nombre) {
            try {
                List<ProductoDto> productos = obtenerTodosLosProductos();
                return productos.stream()
                        .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                        .findFirst()
                        .orElse(null);
            } catch (Exception e) {
                logger.error("❌ Error al buscar producto por nombre: {}", nombre, e);
                return null;
            }
        }


    }
