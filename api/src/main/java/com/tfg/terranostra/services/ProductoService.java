    package com.tfg.terranostra.services;

    import com.tfg.terranostra.dto.ProductoDto;
    import com.tfg.terranostra.repositories.ProductoRepository;
    import com.tfg.terranostra.models.ProductoModel;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class ProductoService {

        @Autowired
        private ProductoRepository productoRepository;

        private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

        public void agregarProducto(ProductoDto productoDto) {
            ProductoModel producto = getProductoModel(productoDto);

            logger.info("📦 Creando nuevo producto: {}", producto.getNombre());

            try {
                productoRepository.save(producto);
                logger.info("✅ Producto guardado correctamente: {}", producto.getNombre());
            } catch (Exception e) {
                logger.error("❌ Error al guardar el producto: {}", producto.getNombre(), e);
                throw new RuntimeException("Error al guardar el producto.");
            }
        }

        private static ProductoModel getProductoModel(ProductoDto productoDto) {
            ProductoModel producto = new ProductoModel();
            producto.setNombre(productoDto.getNombre());
            producto.setDescripcionBreve(productoDto.getDescripcionBreve());
            producto.setDescripcion(productoDto.getDescripcion());
            producto.setPrecio(productoDto.getPrecio());
            producto.setStock(productoDto.getStock());
            producto.setCategoria(productoDto.getCategoria());
            producto.setImagen(productoDto.getImagen());
            producto.setDescuento(productoDto.getDescuento());
            return producto;
        }

        public List<ProductoDto> obtenerTodosLosProductos() {
            return productoRepository.findAll().stream().map(producto -> {
                ProductoDto dto = new ProductoDto();
                dto.setId(producto.getId());
                dto.setNombre(producto.getNombre());
                dto.setDescripcionBreve(producto.getDescripcionBreve());
                dto.setDescripcion(producto.getDescripcion());
                dto.setPrecio(producto.getPrecio());
                dto.setStock(producto.getStock());
                dto.setCategoria(producto.getCategoria());
                dto.setImagen(producto.getImagen());
                dto.setDescuento(producto.getDescuento());
                dto.setFechaAlta(producto.getFechaAlta());
                dto.setFechaModificacion(producto.getFechaModificacion());
                return dto;
            }).collect(Collectors.toList());
        }

        public Optional<ProductoDto> obtenerProductoPorId(Long id) {
            Optional<ProductoModel> producto = productoRepository.findById(id);
            if (producto.isPresent()) {
                ProductoDto dto = new ProductoDto();
                dto.setId(producto.get().getId());
                dto.setNombre(producto.get().getNombre());
                dto.setDescripcionBreve(producto.get().getDescripcionBreve());
                dto.setDescripcion(producto.get().getDescripcion());
                dto.setPrecio(producto.get().getPrecio());
                dto.setStock(producto.get().getStock());
                dto.setCategoria(producto.get().getCategoria());
                dto.setImagen(producto.get().getImagen());
                dto.setDescuento(producto.get().getDescuento());
                dto.setFechaAlta(producto.get().getFechaAlta());
                dto.setFechaModificacion(producto.get().getFechaModificacion());
                return Optional.of(dto);
            }
            return Optional.empty();
        }

        public void eliminarProducto(Long id) {
            Optional<ProductoModel> productoOptional = productoRepository.findById(id);
            if (productoOptional.isPresent()) {
                logger.info("🗑 Eliminando producto: {}", productoOptional.get().getNombre());
                productoRepository.deleteById(id);
                logger.info("✅ Producto eliminado correctamente. ID: {}", id);
            } else {
                logger.warn("⚠ Producto no encontrado. ID: {}", id);
                throw new RuntimeException("Producto no encontrado.");
            }
        }

        public void editarProducto(Long id, ProductoDto productoDto) {
            Optional<ProductoModel> productoOptional = productoRepository.findById(id);
            if (productoOptional.isPresent()) {
                ProductoModel producto = getProductoModel(productoDto, productoOptional);

                logger.info("✏ Editando producto: {}", producto.getNombre());

                productoRepository.save(producto);
                logger.info("✅ Producto actualizado correctamente: {}", producto.getNombre());
            } else {
                logger.warn("⚠ Producto no encontrado para actualizar. ID: {}", id);
                throw new RuntimeException("Producto no encontrado.");
            }
        }

        public List<ProductoDto> buscarProductosConFiltros(String texto, Boolean disponibles, String categoria) {
            return productoRepository.findAll().stream()
                    .filter(producto -> {
                        boolean coincideTexto = true;
                        boolean coincideCategoria = true;
                        boolean coincideStock = true;

                        if (texto != null && !texto.trim().isEmpty()) {
                            String textoLower = texto.toLowerCase();
                            coincideTexto = producto.getNombre().toLowerCase().contains(textoLower)
                                    || (producto.getDescripcionBreve() != null && producto.getDescripcionBreve().toLowerCase().contains(textoLower));
                        }

                        if (categoria != null && !categoria.trim().isEmpty()) {
                            coincideCategoria = categoria.equalsIgnoreCase(producto.getCategoria());
                        }

                        if (disponibles != null && disponibles) {
                            coincideStock = producto.getStock() > 0; // ✅ Aquí quitamos la comparación con null
                        }

                        return coincideTexto && coincideCategoria && coincideStock;
                    })
                    .map(producto -> {
                        ProductoDto dto = new ProductoDto();
                        dto.setId(producto.getId());
                        dto.setNombre(producto.getNombre());
                        dto.setDescripcionBreve(producto.getDescripcionBreve());
                        dto.setDescripcion(producto.getDescripcion());
                        dto.setPrecio(producto.getPrecio());
                        dto.setStock(producto.getStock());
                        dto.setCategoria(producto.getCategoria());
                        dto.setImagen(producto.getImagen());
                        dto.setDescuento(producto.getDescuento());
                        dto.setFechaAlta(producto.getFechaAlta());
                        dto.setFechaModificacion(producto.getFechaModificacion());
                        return dto;
                    }).collect(Collectors.toList());
        }


        public List<String> obtenerCategorias() {
            return List.of(
                    "Suplementos Naturales",
                    "Tés e Infusiones Naturales",
                    "Superalimentos",
                    "Alimentación Saludable",
                    "Cosmética Natural",
                    "Bienestar y Relax",
                    "Packs y Regalos Naturales"
            );
        }


        private static ProductoModel getProductoModel(ProductoDto productoDto, Optional<ProductoModel> productoOptional) {
            ProductoModel producto = productoOptional.get();

            producto.setNombre(productoDto.getNombre());
            producto.setDescripcionBreve(productoDto.getDescripcionBreve());
            producto.setDescripcion(productoDto.getDescripcion());
            producto.setPrecio(productoDto.getPrecio());
            producto.setStock(productoDto.getStock());
            producto.setCategoria(productoDto.getCategoria());
            producto.setDescuento(productoDto.getDescuento());

            if (productoDto.getImagen() != null && productoDto.getImagen().length > 0) {
                producto.setImagen(productoDto.getImagen());
            }
            return producto;
        }
    }
