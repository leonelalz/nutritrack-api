package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ComidaResponse {
    private Long id;
    private String nombre;
    private TipoComida tipoComida;
    private Integer tiempoElaboracion;
    private List<IngredienteRecetaResponse> ingredientes;
    private NutricionTotal nutricionTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    public static class IngredienteRecetaResponse {
        private Long id;
        private String nombre;
        private BigDecimal cantidad; // gramos
    }
    
    @Data
    @Builder
    public static class NutricionTotal {
        private BigDecimal energiaTotal;
        private BigDecimal proteinasTotal;
        private BigDecimal grasasTotal;
        private BigDecimal carbohidratosTotal;
    }
}
