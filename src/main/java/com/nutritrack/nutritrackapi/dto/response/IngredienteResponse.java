package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class IngredienteResponse {
    private Long id;
    private String nombre;
    private GrupoAlimenticio grupoAlimenticio;
    private BigDecimal energia;
    private BigDecimal proteinas;
    private BigDecimal grasas;
    private BigDecimal carbohidratos;
    private Set<EtiquetaSimpleResponse> etiquetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    public static class EtiquetaSimpleResponse {
        private Long id;
        private String nombre;
    }
}
