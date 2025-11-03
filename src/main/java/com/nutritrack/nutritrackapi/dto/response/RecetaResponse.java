package com.nutritrack.nutritrackapi.dto.response;

import java.math.BigDecimal;

public record RecetaResponse(
        Long id,
        Long idComida,
        Long idIngrediente,
        String nombreIngrediente,
        BigDecimal cantidadIngrediente,
        String unidadMedida
) {}