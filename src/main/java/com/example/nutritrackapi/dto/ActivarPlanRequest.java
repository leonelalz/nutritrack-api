package com.example.nutritrackapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de request para activar un plan.
 * Módulo 4: US-18 - Activar una Meta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivarPlanRequest {
    
    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;

    private LocalDate fechaInicio;
    
    private String notas;
}
