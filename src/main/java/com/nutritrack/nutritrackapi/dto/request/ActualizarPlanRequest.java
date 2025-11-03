package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarPlanRequest {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Max(value = 365, message = "La duración no puede exceder 365 días")
    private Integer duracionDias;

    private Boolean activo;

    private List<Long> etiquetasIds;

    private ObjetivoNutricional objetivo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ObjetivoNutricional {

        private BigDecimal caloriasObjetivo;

        private BigDecimal proteinasObjetivo;

        private BigDecimal grasasObjetivo;

        private BigDecimal carbohidratosObjetivo;

        @Size(max = 500, message = "La descripción del objetivo no puede exceder 500 caracteres")
        private String descripcion;
    }
}
