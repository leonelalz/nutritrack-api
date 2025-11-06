package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de request para activar un plan nutricional.
 * Módulo 4: US-18 - Activar una Meta
 * 
 * UNIT TESTS DOCUMENTADOS:
 * - testActivarPlan_Success() - Activación exitosa sin duplicados
 * - testActivarPlan_RN17_MismoPlanActivoLanzaExcepcion() - RN17 duplicado
 * - testActivarPlan_RN32_ConAlergenosIncompatibles() - RN32 alérgenos
 * 
 * Ver: UsuarioPlanServiceTest.java (37 tests)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para activar un plan nutricional. RN17: No permite duplicar mismo plan activo. RN32: Valida alérgenos del usuario contra ingredientes del plan.")
public class ActivarPlanRequest {
    
    @NotNull(message = "El ID del plan es obligatorio")
    @Schema(description = "ID del plan a activar (debe existir y estar activo)", example = "1", required = true)
    private Long planId;

    @Schema(description = "Fecha de inicio del plan (opcional, por defecto hoy)", example = "2025-11-05")
    private LocalDate fechaInicio;
    
    @Schema(description = "Notas opcionales sobre la activación", example = "Iniciando plan de pérdida de peso")
    private String notas;
}
