package com.nutritrack.nutritrackapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngredienteResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal energia,
        BigDecimal proteinas,
        BigDecimal grasas,
        BigDecimal carbohidratos,
        String grupoAlimenticio,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}