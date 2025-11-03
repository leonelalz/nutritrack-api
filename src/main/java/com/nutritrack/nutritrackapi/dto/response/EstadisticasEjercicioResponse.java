package com.nutritrack.nutritrackapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasEjercicioResponse {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer totalRegistros;
    private Integer duracionTotalMinutos;
    private BigDecimal caloriasTotalesQuemadas;
    private BigDecimal caloriasPromedioDiario;
    private List<EstadisticaEjercicio> ejerciciosMasRealizados; // Top ejercicios
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstadisticaEjercicio {
        private String nombreEjercicio;
        private Integer vecesRealizado;
        private BigDecimal caloriasTotales;
        private Integer duracionTotal;
    }
}
