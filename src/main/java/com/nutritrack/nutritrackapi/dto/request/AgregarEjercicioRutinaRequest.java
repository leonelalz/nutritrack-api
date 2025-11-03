package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarEjercicioRutinaRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    private Long ejercicioId;

    @Min(value = 1, message = "El número de series debe ser al menos 1")
    @Max(value = 20, message = "El número de series no puede exceder 20")
    private Integer series;

    @Min(value = 1, message = "El número de repeticiones debe ser al menos 1")
    @Max(value = 100, message = "El número de repeticiones no puede exceder 100")
    private Integer repeticiones;

    @DecimalMin(value = "0.0", message = "El peso debe ser positivo")
    @Digits(integer = 4, fraction = 2, message = "Formato inválido para peso")
    private BigDecimal peso;

    @NotNull(message = "La duración en minutos es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 180, message = "La duración no puede exceder 180 minutos")
    private Integer duracionMinutos;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
