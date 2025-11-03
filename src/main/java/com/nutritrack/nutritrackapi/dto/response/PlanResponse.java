package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
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
public class PlanResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionDias;
    private Boolean activo;
    private List<EtiquetaSimpleResponse> etiquetas;
    private ObjetivoResponse objetivo;
    private NutricionPromedioResponse nutricionPromedio;
    private Integer totalComidas;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ObjetivoResponse {
        private BigDecimal caloriasObjetivo;
        private BigDecimal proteinasObjetivo;
        private BigDecimal grasasObjetivo;
        private BigDecimal carbohidratosObjetivo;
        private String descripcion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NutricionPromedioResponse {
        private BigDecimal caloriasDiarias;
        private BigDecimal proteinasDiarias;
        private BigDecimal grasasDiarias;
        private BigDecimal carbohidratosDiarios;
    }
}
