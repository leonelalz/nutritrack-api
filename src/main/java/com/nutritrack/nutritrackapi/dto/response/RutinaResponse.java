package com.nutritrack.nutritrackapi.dto.response;

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
public class RutinaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionSemanas;
    private Boolean activo;
    private List<EtiquetaSimpleResponse> etiquetas;
    private Integer totalEjercicios;
    private BigDecimal caloriasEstimadasTotal;
    private Integer duracionTotalMinutos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EtiquetaSimpleResponse {
        private Long id;
        private String nombre;
    }
}
