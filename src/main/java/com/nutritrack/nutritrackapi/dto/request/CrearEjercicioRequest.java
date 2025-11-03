package com.nutritrack.nutritrackapi.dto.request;

import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record CrearEjercicioRequest(
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String nombre,
        
        @NotNull(message = "El tipo de ejercicio es obligatorio")
        TipoEjercicio tipoEjercicio,
        
        MusculoPrincipal musculoPrincipal, // Opcional
        
        @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
        Integer duracion, // Minutos (opcional)
        
        Dificultad dificultad, // Opcional
        
        @DecimalMin(value = "0.0", message = "Las calorías deben ser mayor o igual a 0")
        @Digits(integer = 4, fraction = 2)
        BigDecimal caloriasEstimadas, // Opcional
        
        Set<Long> etiquetasIds // IDs de etiquetas (opcional)
) {}
