package com.terra_nostra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terra_nostra.dto.CrearReseniaDto;
import com.terra_nostra.dto.ProductoDto;
import com.terra_nostra.dto.ReseniaDto;
import com.terra_nostra.service.ProductoService;
import com.terra_nostra.service.ReseniaService;
import com.terra_nostra.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ReseniaService reseniaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("descripcionBreve") String descripcionBreve,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoria") String categoria,
            @RequestParam("descuento") BigDecimal descuento,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        ProductoDto productoDto = construirDto(nombre, descripcion, descripcionBreve, precio, stock, categoria, descuento);

        ProductoDto productoGuardado = productoService.guardarProducto(productoDto, imagen);
        return productoGuardado != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el producto");
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, String>> editarProducto(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("descripcionBreve") String descripcionBreve,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoria") String categoria,
            @RequestParam("descuento") BigDecimal descuento,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        Map<String, String> response = new HashMap<>();
        try {
            ProductoDto productoDto = construirDto(nombre, descripcion, descripcionBreve, precio, stock, categoria, descuento);
            productoService.editarProducto(id, productoDto, imagen);
            response.put("mensaje", "✅ Producto actualizado correctamente.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("mensaje", "❌ Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("mensaje", "❌ Error interno al actualizar producto.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ProductoDto>> listarProductos(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Boolean disponibles,
            @RequestParam(required = false) String categoria) {

        try {
            List<ProductoDto> productos;

            boolean hayFiltros = (texto != null && !texto.trim().isEmpty())
                    || disponibles != null
                    || (categoria != null && !categoria.trim().isEmpty());

            if (hayFiltros) {
                productos = productoService.buscarProductosConFiltros(texto, disponibles, categoria);
            } else {
                productos = productoService.obtenerTodosLosProductos();
            }

            logger.info("✅ Productos obtenidos correctamente. Total: {}", productos.size());
            return ResponseEntity.ok(productos);

        } catch (Exception e) {
            logger.error("❌ Error al filtrar productos:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @GetMapping("obtener/{id}")
    public ResponseEntity<ProductoDto> obtenerProductoPorId(@PathVariable Long id) {
        ProductoDto producto = productoService.obtenerProductoPorId(id);
        return producto != null ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminarProducto(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            productoService.eliminarProducto(id);
            response.put("mensaje", "✅ Producto eliminado correctamente.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", "❌ Error al eliminar el producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        try {
            List<String> categorias = productoService.obtenerCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            logger.error("❌ Error al obtener categorías:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/ver/{slug}")
    public ResponseEntity<ProductoDto> obtenerProductoPorSlug(@PathVariable String slug) {
        try {
            String slugNormalizado = normalizarTexto(slug.replace("-", " "));

            List<ProductoDto> productos = productoService.obtenerTodosLosProductos();

            for (ProductoDto p : productos) {
                String nombreNormalizado = normalizarTexto(p.getNombre());
                String comparacion = nombreNormalizado.replaceAll("[^a-z0-9]+", " ").trim().replaceAll(" +", "-");

                if (comparacion.equals(slugNormalizado.replaceAll(" +", "-"))) {
                    return ResponseEntity.ok(p);
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (Exception e) {
            logger.error("❌ Error al obtener producto por slug '{}': {}", slug, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String normalizarTexto(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")            // elimina tildes y diacríticos
                .toLowerCase()
                .replaceAll("[ñ]", "n")              // opcionalmente reemplazar ñ
                .replaceAll("[^a-z0-9\\s]", "");     // elimina símbolos raros
    }


    private ProductoDto construirDto(String nombre, String descripcion, String descripcionBreve, BigDecimal precio,
                                     Integer stock, String categoria, BigDecimal descuento) {
        ProductoDto dto = new ProductoDto();
        dto.setNombre(nombre);
        dto.setDescripcion(descripcion);
        dto.setDescripcionBreve(
                (descripcionBreve == null || descripcionBreve.trim().isEmpty())
                        ? "Sin descripción breve"
                        : descripcionBreve);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setCategoria(categoria);
        dto.setDescuento(descuento);
        return dto;
    }

    /**
     * Crea una nueva resenia.
     *
     * @param ReseniaDto Datos de la resenia enviados desde el frontend.
     * @return ReseniaDto con la información de la resenia creada.
     */
    @PostMapping("/crear-resenia")
    public ResponseEntity<?> crearResenia(@RequestBody String rawJson, HttpServletRequest request) {
        try {
            // 🔍 Muestra el JSON recibido (opcional)
            logger.info("📦 JSON recibido: {}", rawJson);

            // 🧠 Deserialización manual
            ObjectMapper mapper = new ObjectMapper();
            CrearReseniaDto dto = mapper.readValue(rawJson, CrearReseniaDto.class);

            // 🔐 Obtener token de la cookie
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("SESSIONID".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token == null || !jwtUtil.isTokenValido(token)) {
                logger.warn("❌ Token inválido o no presente.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Sesión no válida. Por favor, inicia sesión."));
            }

            // ✅ Extraer ID del usuario del token
            Long usuarioId = jwtUtil.obtenerUsuarioIdDesdeToken(token);
            if (usuarioId == null) {
                logger.error("❌ No se pudo extraer el ID del usuario del token.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Usuario no identificado."));
            }

            // ✅ Setear usuario manualmente
            dto.setUsuarioId(usuarioId);

            // ✅ Crear la reseña
            ReseniaDto resenia = reseniaService.crearResenia(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resenia);

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Validación fallida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Error interno:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Lista todas las resenias de un producto.
     *
     * @param productoId ID del producto.
     * @return Lista de resenias asociadas al producto.
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ReseniaDto>> listarPorProducto(@PathVariable Long productoId) {
        try {
            List<ReseniaDto> resenias = reseniaService.listarReseniasPorProducto(productoId);
            return ResponseEntity.ok(resenias);
        } catch (Exception e) {
            logger.error("❌ Error al obtener resenias del producto {}: {}", productoId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
