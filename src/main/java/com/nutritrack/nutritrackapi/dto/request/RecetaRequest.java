package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RecetaRequest(

        @NotNull(message = "El ID del ingrediente es obligatorio")
        Long idIngrediente,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0") // RN10
        BigDecimal cantidadIngrediente,

        @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
        String unidadMedida
) {}