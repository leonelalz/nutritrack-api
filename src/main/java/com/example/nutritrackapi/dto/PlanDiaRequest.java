package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para agregar actividades diarias a un plan
 * US-12: Gestionar Meta (configurar días del plan)
 * 
 * MIGRACIÓN: tipoComida ahora puede ser ID o nombre (String)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para programar una comida en un día específico del plan")
public class PlanDiaRequest {

    @NotNull(message = "El número de día es obligatorio")
    @Min(value = 1, message = "El número de día debe ser al menos 1")
    @Schema(description = "Día del plan (1, 2, 3... hasta duracion_dias)", 
            example = "1", required = true)
    private Integer numeroDia;

    @Schema(description = "ID del tipo de comida (usar este o tipoComidaNombre)", example = "1")
    private Long tipoComidaId;

    @Schema(description = "Nombre del tipo de comida (usar este o tipoComidaId)", 
            example = "DESAYUNO",
            allowableValues = {"DESAYUNO", "ALMUERZO", "CENA", "SNACK", "PRE_ENTRENAMIENTO", 
                             "POST_ENTRENAMIENTO", "COLACION", "MERIENDA"})
    private String tipoComidaNombre;

    @NotNull(message = "El ID de la comida es obligatorio")
    @Schema(description = "ID de la comida del catálogo a consumir", 
            example = "15", required = true)
    private Long comidaId;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Schema(description = "Notas adicionales para esta comida", 
            example = "Tomar con agua tibia")
    private String notas;
}
