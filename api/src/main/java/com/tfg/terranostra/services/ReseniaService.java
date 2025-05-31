package com.tfg.terranostra.services;

import com.tfg.terranostra.dto.CrearReseniaDto;
import com.tfg.terranostra.dto.ReseniaDto;
import com.tfg.terranostra.models.*;
import com.tfg.terranostra.repositories.ReseniaRepository;
import com.tfg.terranostra.repositories.UsuarioRepository;
import com.tfg.terranostra.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de resenias de productos.
 */
@Service
public class ReseniaService {

    @Autowired
    private ReseniaRepository reseniaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Crea y guarda una nueva resenia en la base de datos.
     *
     * @param dto DTO con los datos necesarios para crear una resenia.
     * @return ReseniaModel guardado.
     */
    public ReseniaModel crearResenia(CrearReseniaDto dto) {
        UsuarioModel usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProductoModel producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        ReseniaModel resenia = new ReseniaModel();
        resenia.setUsuario(usuario);
        resenia.setProducto(producto);
        resenia.setComentario(dto.getComentario());
        resenia.setValoracion(dto.getValoracion());
        resenia.setFecha(LocalDateTime.now());

        return reseniaRepository.save(resenia);
    }

    /**
     * Convierte una entidad ReseniaModel en un DTO de salida ReseniaDto.
     *
     * @param resenia Entidad de resenia a convertir.
     * @return DTO con los datos a mostrar.
     */
    public ReseniaDto convertirAReseniaDto(ReseniaModel resenia) {
        ReseniaDto dto = new ReseniaDto();
        dto.setId(resenia.getId());
        dto.setUsuarioId(resenia.getUsuario().getId());
        dto.setNombreUsuario(resenia.getUsuario().getNombre());
        dto.setProductoId(resenia.getProducto().getId());
        dto.setNombreProducto(resenia.getProducto().getNombre());
        dto.setComentario(resenia.getComentario());
        dto.setValoracion(resenia.getValoracion());
        dto.setFecha(resenia.getFecha());
        return dto;
    }

    public List<ReseniaDto> obtenerReseniasPorProducto(Long productoId) {
        ProductoModel producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        List<ReseniaModel> resenias = reseniaRepository.findByProductoOrderByFechaDesc(producto);

        return resenias.stream()
                .map(this::convertirAReseniaDto)
                .toList();
    }

}
