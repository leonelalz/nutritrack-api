package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AsignarEtiquetasRequest(

        @NotEmpty(message = "Debe proporcionar al menos una etiqueta")
        List<Long> etiquetasIds
) {}