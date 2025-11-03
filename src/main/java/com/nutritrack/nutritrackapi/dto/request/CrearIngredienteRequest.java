package com.nutritrack.nutritrackapi.dto.request;

import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record CrearIngredienteRequest(
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,
        
        @NotNull(message = "El grupo alimenticio es obligatorio")
        GrupoAlimenticio grupoAlimenticio,
        
        @NotNull(message = "La energía es obligatoria")
        @DecimalMin(value = "0.0", message = "La energía debe ser mayor o igual a 0")
        @Digits(integer = 3, fraction = 2, message = "La energía debe tener máximo 3 dígitos enteros y 2 decimales")
        BigDecimal energia, // kcal por 100g
        
        @NotNull(message = "Las proteínas son obligatorias")
        @DecimalMin(value = "0.0", message = "Las proteínas deben ser mayor o igual a 0")
        @Digits(integer = 3, fraction = 2)
        BigDecimal proteinas, // g por 100g
        
        @NotNull(message = "Las grasas son obligatorias")
        @DecimalMin(value = "0.0", message = "Las grasas deben ser mayor o igual a 0")
        @Digits(integer = 3, fraction = 2)
        BigDecimal grasas, // g por 100g
        
        @NotNull(message = "Los carbohidratos son obligatorios")
        @DecimalMin(value = "0.0", message = "Los carbohidratos deben ser mayor o igual a 0")
        @Digits(integer = 3, fraction = 2)
        BigDecimal carbohidratos, // g por 100g
        
        Set<Long> etiquetasIds // IDs de etiquetas (opcional)
) {}
