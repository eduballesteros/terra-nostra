package com.tfg.terranostra.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.terranostra.dto.CarritoDto;
import com.tfg.terranostra.dto.CarritoItemDto;
import com.tfg.terranostra.dto.CrearPedidoDto;
import com.tfg.terranostra.mapper.CarritoMapper;
import com.tfg.terranostra.models.*;
import com.tfg.terranostra.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class CarritoService {

    private static final Logger logger = LoggerFactory.getLogger(CarritoService.class);

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository itemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    // Obtener carrito
    public CarritoDto obtenerCarritoPorUsuario(Long usuarioId) {
        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CarritoModel carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    CarritoModel nuevo = CarritoModel.builder()
                            .usuario(usuario)
                            .items(new ArrayList<>())
                            .build();
                    return carritoRepository.save(nuevo);
                });

        CarritoDto dto = CarritoMapper.aDto(carrito);

        logger.info("✅ Carrito obtenido correctamente para el usuario {}", usuarioId);

        return dto;
    }

    public void agregarProductoAlCarrito(Long usuarioId, CarritoItemDto itemDto) {
        logger.info("🧪 [agregarProductoAlCarrito] Iniciando para usuarioId={} y productoId={}", usuarioId, itemDto.getProductoId());

        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.warn("❌ Usuario no encontrado con ID {}", usuarioId);
                    return new IllegalArgumentException("Usuario no encontrado");
                });

        logger.info("✅ Usuario encontrado: {}", usuario.getEmail());

        ProductoModel producto = productoRepository.findById(itemDto.getProductoId())
                .orElseThrow(() -> {
                    logger.warn("❌ Producto no encontrado con ID {}", itemDto.getProductoId());
                    return new IllegalArgumentException("Producto no encontrado");
                });

        logger.info("✅ Producto encontrado: {}", producto.getNombre());

        CarritoModel carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    logger.info("🛒 No se encontró carrito para el usuario. Creando nuevo...");
                    CarritoModel nuevo = CarritoModel.builder()
                            .usuario(usuario)
                            .items(new ArrayList<>())
                            .build();
                    CarritoModel guardado = carritoRepository.save(nuevo);
                    logger.info("🆕 Carrito creado con ID {}", guardado.getId());
                    return guardado;
                });

        Optional<CarritoItemModel> existente = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId() == producto.getId())
                .findFirst();

        if (existente.isPresent()) {
            CarritoItemModel item = existente.get();
            int cantidadAnterior = item.getCantidad();
            item.setCantidad(cantidadAnterior + itemDto.getCantidad());
            logger.info("🧾 Item ya existente. Cantidad actualizada: {} -> {}", cantidadAnterior, item.getCantidad());
        } else {
            CarritoItemModel nuevoItem = CarritoItemModel.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(itemDto.getCantidad())
                    .precioUnitario(itemDto.getPrecioUnitario())
                    .nombre(itemDto.getNombre())
                    .imagen(itemDto.getImagen())
                    .build();

            carrito.getItems().add(nuevoItem);
            logger.info("🆕 Item añadido al carrito: {} ({} uds)", producto.getNombre(), itemDto.getCantidad());
        }

        carritoRepository.save(carrito);
        logger.info("✅ Carrito guardado correctamente");
    }


    // Vaciar carrito
    public void vaciarCarrito(Long usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            carritoRepository.findByUsuario(usuario).ifPresent(carrito -> {
                carrito.getItems().clear();
                carritoRepository.save(carrito);
            });
        });
    }

    // Finalizar compra
    public void finalizarCompraDesdeCarrito(CrearPedidoDto dto) {
        UsuarioModel usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CarritoModel carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe un carrito para este usuario"));

        if (carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        // Aquí ya no se recalculan los productos desde el carrito — se usan los del dto directamente
        pedidoService.crearPedido(dto);

        carrito.getItems().clear();
        carritoRepository.save(carrito);
        logger.info("✅ Pedido creado y carrito vaciado para el usuario {}", usuario.getId());
    }


    /**
     * Ajusta la cantidad de un producto en el carrito del usuario.
     * Si nuevaCantidad == 0, elimina el item.
     */
    @Transactional
    public void actualizarCantidadProducto(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CarritoModel carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));

        CarritoItemModel item = carrito.getItems().stream()
                .filter(i -> Objects.equals(i.getProducto().getId(), productoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe en el carrito"));

        if (nuevaCantidad == 0) {
            carrito.getItems().remove(item);
            itemRepository.delete(item);
            logger.info("🗑️ Producto {} eliminado (cantidad a 0)", productoId);
        } else {
            item.setCantidad(nuevaCantidad);
            itemRepository.save(item);
            logger.info("🔄 Cantidad de {} actualizada a {}", productoId, nuevaCantidad);
        }
    }

    /**
     * Elimina por completo un producto del carrito del usuario.
     */
    @Transactional
    public void eliminarProducto(Long usuarioId, Long productoId) {
        // 1) validar existencia de usuario
        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2) cargar su carrito
        CarritoModel carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));

        // 3) buscar el ítem
        CarritoItemModel item = carrito.getItems().stream()
                .filter(i -> Objects.equals(i.getProducto().getId(), productoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe en el carrito"));

        // 4) eliminarlo completamente
        carrito.getItems().remove(item);
        itemRepository.delete(item);

        logger.info("🗑️ Producto {} eliminado completamente del carrito", productoId);
    }

}



