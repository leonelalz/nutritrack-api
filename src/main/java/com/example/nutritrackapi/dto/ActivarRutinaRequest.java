package com.example.nutritrackapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de request para activar una rutina.
 * Módulo 4: US-18 - Activar una Meta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivarRutinaRequest {
    
    @NotNull(message = "El ID de la rutina es obligatorio")
    private Long rutinaId;

    private LocalDate fechaInicio;
    
    private String notas;
}
