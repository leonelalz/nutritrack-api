package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearRutinaRequest {

    @NotBlank(message = "El nombre de la rutina es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La duración en semanas es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 semana")
    @Max(value = 52, message = "La duración no puede exceder 52 semanas")
    private Integer duracionSemanas;

    private List<Long> etiquetasIds;
}
