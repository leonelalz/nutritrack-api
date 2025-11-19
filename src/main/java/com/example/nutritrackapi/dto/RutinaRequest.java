package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ejercicio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para crear o actualizar una rutina de ejercicio
 * US-11: Crear Meta del Catálogo (Rutina)
 * US-12: Gestionar Meta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar una rutina de ejercicio")
public class RutinaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    @Schema(description = "Nombre de la rutina (RN11: debe ser único)", 
            example = "Rutina Hipertrofia 12 Semanas", required = true)
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    @Schema(description = "Descripción de la rutina", 
            example = "Programa de entrenamiento enfocado en ganancia de masa muscular")
    private String descripcion;

    @NotNull(message = "La duración en semanas es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 semana")
    @Max(value = 52, message = "La duración no puede exceder 52 semanas")
    @Schema(description = "Duración de la rutina en semanas", example = "12", required = true)
    private Integer duracionSemanas;

    @NotNull(message = "El patrón de semanas es obligatorio")
    @Min(value = 1, message = "El patrón debe ser al menos 1 semana")
    @Schema(description = "Número de semanas base que se repiten (ej: 2 = patrón de 2 semanas que se repite)", 
            example = "2", required = true)
    private Integer patronSemanas;

    @Schema(description = "Nivel de dificultad de la rutina", example = "INTERMEDIO",
            allowableValues = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO", "EXPERTO"})
    private Ejercicio.NivelDificultad nivelDificultad;

    @Schema(description = "IDs de etiquetas para clasificar la rutina (RN12: solo etiquetas existentes)", 
            example = "[1, 2, 3]")
    private Set<Long> etiquetaIds;
}
