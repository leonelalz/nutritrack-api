package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de request para registrar un ejercicio.
 * Módulo 5: US-22 - Marcar Actividad como Completada
 * 
 * Nota: Los campos series, repeticiones y duracionMinutos son opcionales
 * porque no todos los ejercicios requieren todos estos valores:
 * - Ejercicios de fuerza: series + repeticiones (sin duración)
 * - Ejercicios de cardio: duración (sin series/repeticiones)
 * - Ejercicios isométricos: series + duración (sin repeticiones)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registrar un ejercicio realizado")
public class RegistroEjercicioRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    @Schema(description = "ID del ejercicio del catálogo (Sentadillas)", example = "9", required = true)
    private Long ejercicioId;

    @Schema(description = "ID de la rutina activa (opcional)", example = "1")
    private Long usuarioRutinaId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha del ejercicio (YYYY-MM-DD)", example = "2025-11-05", required = true)
    private LocalDate fecha;

    @Schema(description = "Hora del ejercicio (HH:mm:ss)", example = "18:30:00")
    private LocalTime hora;

    @PositiveOrZero(message = "Las series deben ser un número positivo o cero")
    @Schema(description = "Número de series realizadas (opcional para cardio)", example = "3")
    private Integer series;

    @PositiveOrZero(message = "Las repeticiones deben ser un número positivo o cero")
    @Schema(description = "Repeticiones por serie (opcional para cardio/isométricos)", example = "10")
    private Integer repeticiones;

    @PositiveOrZero(message = "El peso debe ser un número positivo o cero")
    @Schema(description = "Peso utilizado en kg", example = "20.0")
    private BigDecimal pesoKg;

    @PositiveOrZero(message = "La duración debe ser un número positivo o cero")
    @Schema(description = "Duración total en minutos (opcional para ejercicios de repeticiones)", example = "15")
    private Integer duracionMinutos;

    @PositiveOrZero(message = "Las calorías quemadas deben ser un número positivo o cero")
    @Schema(description = "Calorías quemadas calculadas por el frontend (opcional, si no se envía se calculan automáticamente)", example = "150")
    private BigDecimal caloriasQuemadas;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Schema(description = "Notas adicionales", example = "Buen entrenamiento, sin dolor")
    private String notas;
}
