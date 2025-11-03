package com.nutritrack.nutritrackapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record EjercicioResponse(
        Long id,
        String nombre,
        String descripcion,
        String instrucciones,
        Integer duracionMinutos,
        Integer caloriasQuemadas,
        String videoUrl,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}