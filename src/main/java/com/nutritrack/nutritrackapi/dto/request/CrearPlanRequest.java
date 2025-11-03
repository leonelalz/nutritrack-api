package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.*;
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
public class CrearPlanRequest {

    @NotBlank(message = "El nombre del plan es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Max(value = 365, message = "La duración no puede exceder 365 días")
    private Integer duracionDias;

    private List<Long> etiquetasIds;

    private ObjetivoNutricional objetivo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ObjetivoNutricional {

        @DecimalMin(value = "0.0", message = "Las calorías deben ser positivas")
        @Digits(integer = 8, fraction = 2, message = "Formato inválido para calorías")
        private BigDecimal caloriasObjetivo;

        @DecimalMin(value = "0.0", message = "Las proteínas deben ser positivas")
        @Digits(integer = 8, fraction = 2, message = "Formato inválido para proteínas")
        private BigDecimal proteinasObjetivo;

        @DecimalMin(value = "0.0", message = "Las grasas deben ser positivas")
        @Digits(integer = 8, fraction = 2, message = "Formato inválido para grasas")
        private BigDecimal grasasObjetivo;

        @DecimalMin(value = "0.0", message = "Los carbohidratos deben ser positivos")
        @Digits(integer = 8, fraction = 2, message = "Formato inválido para carbohidratos")
        private BigDecimal carbohidratosObjetivo;

        @Size(max = 500, message = "La descripción del objetivo no puede exceder 500 caracteres")
        private String descripcion;
    }
}
