package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarRutinaRequest {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Min(value = 1, message = "La duración debe ser al menos 1 semana")
    @Max(value = 52, message = "La duración no puede exceder 52 semanas")
    private Integer duracionSemanas;

    private Boolean activo;

    private List<Long> etiquetasIds;
}
