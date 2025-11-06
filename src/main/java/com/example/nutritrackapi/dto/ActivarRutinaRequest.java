package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de request para activar una rutina de ejercicios.
 * Módulo 4: US-18 - Activar una Meta
 * 
 * UNIT TESTS DOCUMENTADOS:
 * - testActivarRutina_Success() - Activación exitosa sin duplicados
 * - testActivarRutina_RN17_MismaRutinaActivaLanzaExcepcion() - RN17 duplicado
 * - testActivarRutina_RutinaInactiva() - Rutina desactivada
 * 
 * Ver: UsuarioRutinaServiceTest.java
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para activar una rutina de ejercicios. RN17: No permite duplicar misma rutina activa.")
public class ActivarRutinaRequest {
    
    @NotNull(message = "El ID de la rutina es obligatorio")
    @Schema(description = "ID de la rutina a activar (Rutina Principiante - 4 semanas)", example = "1", required = true)
    private Long rutinaId;

    @Schema(description = "Fecha de inicio de la rutina (opcional, por defecto hoy)", example = "2025-11-05")
    private LocalDate fechaInicio;
    
    @Schema(description = "Notas opcionales sobre la activación", example = "Iniciando rutina principiante - 4 semanas")
    private String notas;
}
