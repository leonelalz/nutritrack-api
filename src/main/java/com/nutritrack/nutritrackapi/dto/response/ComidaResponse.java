package com.nutritrack.nutritrackapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ComidaResponse(
        Long id,
        String nombre,
        String descripcion,
        String tipoComida,
        Integer tiempoPreparacion,
        String instruccionesPreparacion,
        Integer porciones,
        String imagenUrl,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}