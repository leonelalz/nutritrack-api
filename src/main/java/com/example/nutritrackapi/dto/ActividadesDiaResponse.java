package com.example.nutritrackapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta para actividades del día del plan.
 * Módulo 5: US-21 - Ver Actividades de mi Plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadesDiaResponse {

    private LocalDate fecha;
    private Integer diaActual;
    private BigDecimal caloriasObjetivo;
    private BigDecimal caloriasConsumidas;
    private List<ComidaDiaInfo> comidas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComidaDiaInfo {
        private Long comidaId;
        private String nombre;
        private String tipoComida;
        private BigDecimal calorias;
        private boolean registrada;
        private Long registroId; // ID del registro si ya fue marcada
    }
}
