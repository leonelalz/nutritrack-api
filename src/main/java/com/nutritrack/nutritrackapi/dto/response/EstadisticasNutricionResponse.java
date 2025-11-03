package com.nutritrack.nutritrackapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasNutricionResponse {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer totalRegistros;
    private BigDecimal caloriasPromedioDiario;
    private BigDecimal caloriasTotales;
    private Map<String, EstadisticaTipoComida> estadisticasPorTipo; // TipoComida → stats
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstadisticaTipoComida {
        private Integer cantidad;
        private BigDecimal caloriasTotales;
        private BigDecimal caloriasPromedio;
    }
}
