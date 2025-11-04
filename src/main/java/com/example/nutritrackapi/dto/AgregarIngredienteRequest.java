package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para agregar un ingrediente a una comida (crear receta)
 * US-10: Gestionar Recetas
 * RN10: Cantidad debe ser positiva
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para agregar un ingrediente a una comida")
public class AgregarIngredienteRequest {

    @NotNull(message = "El ID del ingrediente es obligatorio")
    @Positive(message = "El ID del ingrediente debe ser positivo")
    @Schema(description = "ID del ingrediente a agregar", example = "1", required = true)
    private Long ingredienteId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @DecimalMax(value = "10000.0", message = "La cantidad no puede exceder 10000 gramos")
    @Schema(description = "Cantidad del ingrediente en gramos", example = "150.0", required = true)
    private BigDecimal cantidadGramos;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Schema(description = "Notas adicionales sobre el ingrediente", 
            example = "Cocinar a la plancha sin aceite")
    private String notas;
}
