package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para agregar ejercicios a una rutina
 * US-12: Gestionar Meta (configurar ejercicios)
 * US-15: Ensamblar Rutinas
 * RN13: Series y repeticiones deben ser valores numéricos positivos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para configurar un ejercicio dentro de una rutina")
public class RutinaEjercicioRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    @Schema(description = "ID del ejercicio del catálogo", example = "5", required = true)
    private Long ejercicioId;

    @NotNull(message = "La semana base es obligatoria")
    @Min(value = 1, message = "La semana base debe ser al menos 1")
    @Schema(description = "Semana base del patrón (debe ser <= patronSemanas de la rutina)", 
            example = "1", required = true)
    private Integer semanaBase;

    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día debe ser entre 1 (Lunes) y 7 (Domingo)")
    @Max(value = 7, message = "El día debe ser entre 1 (Lunes) y 7 (Domingo)")
    @Schema(description = "Día de la semana (1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado, 7=Domingo)", 
            example = "1", required = true)
    private Integer diaSemana;

    @NotNull(message = "El orden es obligatorio")
    @Min(value = 1, message = "El orden debe ser al menos 1")
    @Schema(description = "Orden de ejecución dentro del mismo día (1, 2, 3...)", example = "1", required = true)
    private Integer orden;

    @NotNull(message = "El número de series es obligatorio")
    @Min(value = 1, message = "RN13: Las series deben ser al menos 1")
    @Max(value = 20, message = "Las series no pueden exceder 20")
    @Schema(description = "Número de series (RN13: valor positivo)", example = "4", required = true)
    private Integer series;

    @NotNull(message = "El número de repeticiones es obligatorio")
    @Min(value = 1, message = "RN13: Las repeticiones deben ser al menos 1")
    @Max(value = 100, message = "Las repeticiones no pueden exceder 100")
    @Schema(description = "Número de repeticiones por serie (RN13: valor positivo)", 
            example = "12", required = true)
    private Integer repeticiones;

    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
    @DecimalMax(value = "500.0", message = "El peso no puede exceder 500 kg")
    @Schema(description = "Peso sugerido en kg (opcional)", example = "60.0")
    private BigDecimal peso;

    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 180, message = "La duración no puede exceder 180 minutos")
    @Schema(description = "Duración total del ejercicio en minutos", example = "15")
    private Integer duracionMinutos;

    @Min(value = 0, message = "El descanso no puede ser negativo")
    @Max(value = 600, message = "El descanso no puede exceder 600 segundos")
    @Schema(description = "Tiempo de descanso entre series en segundos", example = "90")
    private Integer descansoSegundos;

    @Size(max = 2000, message = "Las notas no pueden exceder 2000 caracteres")
    @Schema(description = "Notas adicionales del ejercicio", 
            example = "Bajar hasta 90 grados, mantener la espalda recta")
    private String notas;
}
