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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registrar un ejercicio realizado")
public class RegistroEjercicioRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    @Schema(description = "ID del ejercicio del catálogo", example = "1", required = true)
    private Long ejercicioId;

    @Schema(description = "ID de la rutina activa (opcional)", example = "1")
    private Long usuarioRutinaId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha del ejercicio (YYYY-MM-DD)", example = "2025-11-05", required = true)
    private LocalDate fecha;

    @Schema(description = "Hora del ejercicio (HH:mm:ss)", example = "18:30:00")
    private LocalTime hora;

    @Positive(message = "Las series deben ser un número positivo")
    @Schema(description = "Número de series realizadas", example = "3")
    private Integer series;

    @Positive(message = "Las repeticiones deben ser un número positivo")
    @Schema(description = "Repeticiones por serie", example = "10")
    private Integer repeticiones;

    @PositiveOrZero(message = "El peso debe ser un número positivo o cero")
    @Schema(description = "Peso utilizado en kg", example = "20.0")
    private BigDecimal pesoKg;

    @Positive(message = "La duración debe ser un número positivo")
    @Schema(description = "Duración total en minutos", example = "15")
    private Integer duracionMinutos;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Schema(description = "Notas adicionales", example = "Buen entrenamiento, sin dolor")
    private String notas;
}
