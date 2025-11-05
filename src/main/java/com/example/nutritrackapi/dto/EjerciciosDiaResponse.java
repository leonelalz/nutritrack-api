package com.example.nutritrackapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para ejercicios del día de la rutina.
 * Módulo 5: US-21 - Ver Actividades de mi Plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjerciciosDiaResponse {

    private java.time.LocalDate fecha;
    private Integer semanaActual;
    private List<EjercicioDiaInfo> ejercicios;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EjercicioDiaInfo {
        private Long ejercicioId;
        private String nombre;
        private Integer seriesObjetivo;
        private Integer repeticionesObjetivo;
        private java.math.BigDecimal pesoSugerido;
        private Integer duracionMinutos;
        private boolean registrado;
        private Long registroId; // ID del registro si ya fue marcado
    }
}
