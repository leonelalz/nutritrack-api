package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutinaDetalleResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionSemanas;
    private Boolean activo;
    private List<RutinaResponse.EtiquetaSimpleResponse> etiquetas;
    private List<EjercicioRutinaResponse> ejercicios;
    private EstadisticasRutinaResponse estadisticas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EjercicioRutinaResponse {
        private Long id;
        private Integer orden;
        private EjercicioSimpleResponse ejercicio;
        private Integer series;
        private Integer repeticiones;
        private BigDecimal peso;
        private Integer duracionMinutos;
        private BigDecimal caloriasEstimadas;
        private String notas;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EjercicioSimpleResponse {
        private Long id;
        private String nombre;
        private TipoEjercicio tipoEjercicio;
        private MusculoPrincipal musculoPrincipal;
        private Dificultad dificultad;
        private BigDecimal caloriasEstimadas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstadisticasRutinaResponse {
        private Integer totalEjercicios;
        private Integer totalSeries;
        private Integer totalRepeticiones;
        private BigDecimal caloriasEstimadasTotal;
        private Integer duracionTotalMinutos;
    }
}
