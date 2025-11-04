package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para ingredientes de una comida (receta)
 * US-10: Gestionar Recetas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ingrediente de una comida con su cantidad")
public class RecetaIngredienteResponse {

    @Schema(description = "ID del ingrediente", example = "1")
    private Long ingredienteId;

    @Schema(description = "Nombre del ingrediente", example = "Pechuga de pollo")
    private String ingredienteNombre;

    @Schema(description = "Cantidad en gramos", example = "150.0")
    private BigDecimal cantidadGramos;

    @Schema(description = "Proteínas aportadas (g)", example = "46.5")
    private BigDecimal proteinasAportadas;

    @Schema(description = "Carbohidratos aportados (g)", example = "0.0")
    private BigDecimal carbohidratosAportados;

    @Schema(description = "Grasas aportadas (g)", example = "5.4")
    private BigDecimal grasasAportadas;

    @Schema(description = "Energía aportada (kcal)", example = "247.5")
    private BigDecimal energiaAportada;

    @Schema(description = "Fibra aportada (g)", example = "0.0")
    private BigDecimal fibraAportada;

    @Schema(description = "Notas adicionales")
    private String notas;
}
