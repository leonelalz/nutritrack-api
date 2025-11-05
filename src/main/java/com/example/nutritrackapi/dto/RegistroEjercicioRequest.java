package com.example.nutritrackapi.dto;

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
public class RegistroEjercicioRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    private Long ejercicioId;

    private Long usuarioRutinaId; // Opcional: si es parte de una rutina activa

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private LocalTime hora; // Opcional, se usa hora actual si no se proporciona

    @Positive(message = "Las series deben ser un número positivo")
    private Integer series;

    @Positive(message = "Las repeticiones deben ser un número positivo")
    private Integer repeticiones;

    @PositiveOrZero(message = "El peso debe ser un número positivo o cero")
    private BigDecimal pesoKg;

    @Positive(message = "La duración debe ser un número positivo")
    private Integer duracionMinutos;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
