package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ejercicio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO para crear o actualizar un ejercicio
 * US-08: Gestionar Ejercicios
 * RN07: Nombre único
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un ejercicio")
public class EjercicioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    @Schema(description = "Nombre del ejercicio", example = "Sentadillas", required = true)
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Schema(description = "Descripción del ejercicio", 
            example = "Ejercicio compuesto que trabaja piernas y glúteos. Mantén la espalda recta y desciende hasta que los muslos estén paralelos al suelo.")
    private String descripcion;

    @NotNull(message = "El tipo de ejercicio es obligatorio")
    @Schema(description = "Tipo de ejercicio", example = "FUERZA", required = true,
            allowableValues = {"CARDIO", "FUERZA", "FLEXIBILIDAD", "EQUILIBRIO", "HIIT", 
                             "YOGA", "PILATES", "FUNCIONAL", "DEPORTIVO", "REHABILITACION", "OTRO"})
    private Ejercicio.TipoEjercicio tipoEjercicio;

    @NotNull(message = "El grupo muscular es obligatorio")
    @Schema(description = "Grupo muscular principal trabajado", example = "PIERNAS", required = true,
            allowableValues = {"PECHO", "ESPALDA", "HOMBROS", "BRAZOS", "BICEPS", "TRICEPS", 
                             "ABDOMINALES", "PIERNAS", "CUADRICEPS", "ISQUIOTIBIALES", "GLUTEOS", 
                             "GEMELOS", "CORE", "CARDIO", "CUERPO_COMPLETO", "OTRO"})
    private Ejercicio.GrupoMuscular grupoMuscular;

    @NotNull(message = "El nivel de dificultad es obligatorio")
    @Schema(description = "Nivel de dificultad", example = "INTERMEDIO", required = true,
            allowableValues = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO", "EXPERTO"})
    private Ejercicio.NivelDificultad nivelDificultad;

    @DecimalMin(value = "0.0", message = "Las calorías quemadas no pueden ser negativas")
    @DecimalMax(value = "50.0", message = "Las calorías quemadas por minuto no pueden exceder 50")
    @Schema(description = "Calorías quemadas por minuto", example = "8.5")
    private BigDecimal caloriasQuemadasPorMinuto;

    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 300, message = "La duración no puede exceder 300 minutos")
    @Schema(description = "Duración estimada en minutos", example = "15")
    private Integer duracionEstimadaMinutos;

    @Size(max = 500, message = "El equipo necesario no puede exceder 500 caracteres")
    @Schema(description = "Equipo necesario para realizar el ejercicio", 
            example = "Barra olímpica, discos de peso")
    private String equipoNecesario;

    @Schema(description = "IDs de etiquetas a asignar", example = "[1, 2, 3]")
    private Set<Long> etiquetaIds;
}
