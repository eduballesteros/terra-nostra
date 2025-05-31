package com.terra_nostra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarritoDto {

    private Long id;

    private Long usuarioId;

    @NotNull
    private LocalDateTime fechaCreacion;

    @NotEmpty
    private List<@Valid CarritoItemDto> items;
}
