package com.tfg.terranostra.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarritoDto {

    private Long id;

    private Long usuarioId;

    private LocalDateTime fechaCreacion;

    private List<CarritoItemDto> items;
}
