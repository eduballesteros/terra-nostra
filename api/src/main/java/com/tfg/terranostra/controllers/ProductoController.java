    package com.tfg.terranostra.controllers;

    import com.tfg.terranostra.dto.ProductoDto;
    import com.tfg.terranostra.services.ProductoService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.BindingResult;
    import org.springframework.validation.ObjectError;
    import org.springframework.web.bind.annotation.*;

    import jakarta.validation.Valid;
    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/productos")

    /**
     * Controlador para la gestión de productos en la aplicación.
     * Proporciona endpoints para agregar, listar, editar, eliminar y obtener productos.
     *
     * @author ebp
     * @version 1.0
     */


    public class ProductoController {

        @Autowired
        private ProductoService productoService;

        private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);


        /**
         * Agrega un nuevo producto a la base de datos.
         * - Valida los datos del producto antes de guardarlo.<br>
         * - Si hay errores de validación, devuelve un código HTTP 400 con los detalles.<br>
         * - En caso de éxito, devuelve el producto guardado con un código HTTP 201.
         *
         * @param productoDto Objeto que contiene la información del producto a agregar.
         * @param result Objeto que maneja los errores de validación.
         * @return `ResponseEntity` con el producto agregado o un mensaje de error si la validación falla.
         */


        @PostMapping("/guardar")
        public ResponseEntity<?> agregarProducto(@Valid @RequestBody ProductoDto productoDto, BindingResult result) {
            if (result.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder("Errores en la validación: ");
                for (ObjectError error : result.getAllErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append(" ");
                }
                logger.error("❌ Error en la validación de datos: {}", errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }

            try {
                logger.info("📥 Se ha recibido la solicitud para agregar el producto: {}", productoDto.getNombre());
                productoService.agregarProducto(productoDto);
                logger.info("✅ Producto agregado exitosamente: {}", productoDto.getNombre());
                return ResponseEntity.status(HttpStatus.CREATED).body(productoDto);
            } catch (Exception e) {
                logger.error("❌ Error al añadir el producto: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir el producto.");
            }
        }

        /**
         * Obtiene la lista de todos los productos almacenados en la base de datos.
         *
         * @return `ResponseEntity` con una lista de productos o una lista vacía en caso de error.
         */


        @GetMapping("/listar")
        public ResponseEntity<List<ProductoDto>> listarProductos(
                @RequestParam(required = false) String texto,
                @RequestParam(required = false) Boolean disponibles,
                @RequestParam(required = false) String categoria) {
            try {
                List<ProductoDto> productos;

                if ((texto != null && !texto.trim().isEmpty()) || disponibles != null || (categoria != null && !categoria.trim().isEmpty())) {
                    productos = productoService.buscarProductosConFiltros(texto, disponibles, categoria);
                } else {
                    productos = productoService.obtenerTodosLosProductos();
                }

                return ResponseEntity.ok(productos);
            } catch (Exception e) {
                logger.error("❌ Error al obtener productos:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
            }
        }

        /**
         * Elimina un producto de la base de datos según su ID.
         *
         * - Verifica si el producto existe antes de eliminarlo.<br>
         * - Si el producto no se encuentra, devuelve un código HTTP 404.<br>
         * - En caso de éxito, devuelve un mensaje confirmando la eliminación.
         *
         * @param id Identificador único del producto a eliminar.
         * @return `ResponseEntity` con un mensaje de éxito o error.
         */


        @DeleteMapping("/eliminar/{id}")
        public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
            try {
                Optional<ProductoDto> productoOptional = productoService.obtenerProductoPorId(id);
                if (productoOptional.isPresent()) {
                    productoService.eliminarProducto(id);
                    logger.info("✅ Producto eliminado correctamente. ID: {}", id);
                    return ResponseEntity.ok().body("{\"mensaje\": \"✅ Producto eliminado correctamente.\"}");
                } else {
                    logger.warn("⚠ Producto no encontrado. ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"mensaje\": \"❌ Producto no encontrado.\"}");
                }
            } catch (Exception e) {
                logger.error("❌ Error al eliminar el producto con ID {}: {}", id, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"mensaje\": \"❌ Error al eliminar el producto.\"}");
            }
        }

        /**
         * Actualiza la información de un producto en la base de datos.
         * - Valida los datos antes de actualizar.<br>
         * - Si el producto no se encuentra, devuelve un código HTTP 404.<br>
         * - Si la actualización es exitosa, devuelve un mensaje de confirmación.
         *
         * @param id Identificador único del producto a actualizar.
         * @param productoActualizado Objeto `ProductoDto` con los nuevos datos del producto.
         * @param result Objeto que maneja los errores de validación.
         * @return `ResponseEntity` con un mensaje de éxito o error.
         */


        @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> editarProducto(
                @PathVariable Long id,
                @Valid @RequestBody ProductoDto productoActualizado,
                BindingResult result) {
            if (result.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder("Errores en la validación: ");
                for (ObjectError error : result.getAllErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append(" ");
                }
                logger.error("❌ Error en la validación de datos para edición: {}", errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("mensaje", errorMessage.toString()));
            }

            try {
                Optional<ProductoDto> productoOptional = productoService.obtenerProductoPorId(id);
                if (productoOptional.isPresent()) {
                    productoService.editarProducto(id, productoActualizado);
                    logger.info("✅ Producto actualizado correctamente. ID: {}", id);
                    return ResponseEntity.ok(Collections.singletonMap("mensaje", "✅ Producto actualizado correctamente."));
                } else {
                    logger.warn("⚠ Producto no encontrado. ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("mensaje", "❌ Producto no encontrado."));
                }
            } catch (Exception e) {
                logger.error("❌ Error al actualizar el producto con ID {}: {}", id, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("mensaje", "❌ Error al actualizar el producto."));
            }
        }

        /**
         * Obtiene los detalles de un producto según su ID.

         * - Si el producto no se encuentra, devuelve un código HTTP 404 con un mensaje de error.<br>
         * - En caso de éxito, devuelve la información del producto solicitado.
         *
         * @param id Identificador único del producto a buscar.
         * @return `ResponseEntity` con los detalles del producto o un mensaje de error si no se encuentra.
         */


        @GetMapping("/obtener/{id}")
        public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
            logger.info("📥 Solicitando producto con ID: {}", id);

            Optional<ProductoDto> productoOptional = productoService.obtenerProductoPorId(id);

            if (productoOptional.isPresent()) {
                return ResponseEntity.ok(productoOptional.get());
            } else {
                logger.warn("⚠ Producto no encontrado. ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"mensaje\": \"❌ Producto no encontrado.\"}");
            }
        }

        @GetMapping("/redirigir/{id}")
        public ResponseEntity<String> redirigirPorId(@PathVariable Long id) {
            Optional<ProductoDto> productoOpt = productoService.obtenerProductoPorId(id);
            if (productoOpt.isPresent()) {
                ProductoDto producto = productoOpt.get();
                String nombreSlug = producto.getNombre().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
                return ResponseEntity.ok(nombreSlug);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
            }
        }


        @GetMapping("/categorias")
        public ResponseEntity<List<String>> obtenerCategorias() {
            return ResponseEntity.ok(productoService.obtenerCategorias());
        }

    }