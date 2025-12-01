package com.example.nutritrackapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para el progreso acumulado del plan nutricional.
 * Muestra estadísticas completas desde el inicio del plan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoPlanResponse {
    
    // Información del plan
    private Long usuarioPlanId;
    private Long planId;
    private String nombrePlan;
    private LocalDate fechaInicio;
    private LocalDate fechaActual;
    
    // Días del plan
    private Integer diaActual;              // Días desde inicio (puede ser > duracionDias)
    private Integer diaPlanCiclico;         // Día actual dentro del ciclo (1 a duracionDias)
    private Integer duracionDias;           // Duración total del plan
    private Integer cicloActual;            // Número de ciclo (si el plan es cíclico)
    
    // Estadísticas de cumplimiento
    private Integer diasCompletados;        // Días donde se completaron TODAS las comidas
    private Integer diasParciales;          // Días con algunas comidas
    private Integer diasSinRegistro;        // Días sin ningún registro
    private BigDecimal porcentajeDiasCompletados;  // (diasCompletados / diasTranscurridos) * 100
    
    // Estadísticas de comidas
    private Integer totalComidasProgramadas;    // Total de comidas que debieron consumirse
    private Integer totalComidasRegistradas;    // Total de comidas que se registraron
    private BigDecimal porcentajeComidasRegistradas;  // (registradas / programadas) * 100
    
    // Estado del día actual
    private Integer comidasHoyProgramadas;
    private Integer comidasHoyCompletadas;
    private boolean diaActualCompleto;
    
    // Racha actual
    private Integer rachaActual;            // Días consecutivos completados
    private Integer rachaMejor;             // Mejor racha histórica
    
    // Historial de días (últimos 7 o configurable)
    private List<DiaPlanInfo> historialDias;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaPlanInfo {
        private LocalDate fecha;
        private Integer diaPlan;            // Día del plan (1 a duracionDias)
        private Integer comidasProgramadas;
        private Integer comidasCompletadas;
        private boolean completo;           // true si todas las comidas fueron registradas
        private String estado;              // "COMPLETO", "PARCIAL", "SIN_REGISTRO", "FUTURO"
    }
}
