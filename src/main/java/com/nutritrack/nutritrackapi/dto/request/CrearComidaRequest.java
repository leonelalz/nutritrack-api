package com.nutritrack.nutritrackapi.dto.request;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
public record CrearComidaRequest(
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,
        
        @NotNull(message = "El tipo de comida es obligatorio")
        TipoComida tipoComida,
        
        @Min(value = 1, message = "El tiempo de elaboración debe ser al menos 1 minuto")
        Integer tiempoElaboracion, // Minutos (opcional)
        
        List<IngredienteReceta> ingredientes // Lista de ingredientes con cantidades (opcional)
) {
    @Builder
    public record IngredienteReceta(
            @NotNull(message = "El ID del ingrediente es obligatorio")
            Long idIngrediente,
            
            @NotNull(message = "La cantidad es obligatoria")
            @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
            java.math.BigDecimal cantidad // En gramos
    ) {}
}
