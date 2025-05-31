package com.tfg.terranostra.mapper;

import com.tfg.terranostra.dto.CarritoDto;
import com.tfg.terranostra.dto.CarritoItemDto;
import com.tfg.terranostra.models.CarritoModel;
import com.tfg.terranostra.models.CarritoItemModel;

import java.util.List;
import java.util.stream.Collectors;

public class CarritoMapper {

    public static CarritoDto aDto(CarritoModel carrito) {
        CarritoDto dto = new CarritoDto();
        dto.setId(carrito.getId());
        dto.setUsuarioId(carrito.getUsuario().getId());
        dto.setFechaCreacion(carrito.getFechaCreacion());

        List<CarritoItemDto> itemsDto = carrito.getItems().stream()
                .map(CarritoMapper::aItemDto)
                .collect(Collectors.toList());

        dto.setItems(itemsDto);
        return dto;
    }

    public static CarritoItemDto aItemDto(CarritoItemModel item) {
        CarritoItemDto dto = new CarritoItemDto();
        dto.setProductoId(item.getProducto().getId());
        dto.setNombre(item.getProducto().getNombre());

        byte[] imagenBytes = item.getProducto().getImagen();
        if (imagenBytes != null) {
            dto.setImagen(java.util.Base64.getEncoder().encodeToString(imagenBytes));
        }

        dto.setPrecioUnitario(item.getProducto().getPrecio().doubleValue());
        dto.setCantidad(item.getCantidad());
        return dto;
    }
}
