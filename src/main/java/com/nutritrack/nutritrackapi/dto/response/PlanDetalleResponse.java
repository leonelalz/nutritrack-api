package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDetalleResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionDias;
    private Boolean activo;
    private List<PlanResponse.EtiquetaSimpleResponse> etiquetas;
    private PlanResponse.ObjetivoResponse objetivo;
    private Map<Integer, List<DiaComidaResponse>> diasPorNumero; // Agrupado por día
    private NutricionPorDiaResponse nutricionPorDia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaComidaResponse {
        private Long id;
        private Integer numeroDia;
        private TipoComida tipoComida;
        private ComidaSimpleResponse comida;
        private String notas;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComidaSimpleResponse {
        private Long id;
        private String nombre;
        private TipoComida tipoComida;
        private NutricionComidaResponse nutricion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NutricionComidaResponse {
        private BigDecimal energiaTotal;
        private BigDecimal proteinasTotal;
        private BigDecimal grasasTotal;
        private BigDecimal carbohidratosTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NutricionPorDiaResponse {
        private BigDecimal caloriasPorDia;
        private BigDecimal proteinasPorDia;
        private BigDecimal grasasPorDia;
        private BigDecimal carbohidratosPorDia;
        private Integer diasConComidas;
    }
}
