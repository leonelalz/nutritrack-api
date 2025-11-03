package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record IngredienteRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,

        @DecimalMin(value = "0.0", message = "La energía debe ser mayor o igual a 0")
        BigDecimal energia,

        @DecimalMin(value = "0.0", message = "Las proteínas deben ser mayor o igual a 0")
        BigDecimal proteinas,

        @DecimalMin(value = "0.0", message = "Las grasas deben ser mayor o igual a 0")
        BigDecimal grasas,

        @DecimalMin(value = "0.0", message = "Los carbohidratos deben ser mayor o igual a 0")
        BigDecimal carbohidratos,

        @Size(max = 100, message = "El grupo alimenticio no puede exceder 100 caracteres")
        String grupoAlimenticio
) {}