package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EtiquetaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,

        @Size(max = 50, message = "El tipo de etiqueta no puede exceder 50 caracteres")
        String tipoEtiqueta
) {}