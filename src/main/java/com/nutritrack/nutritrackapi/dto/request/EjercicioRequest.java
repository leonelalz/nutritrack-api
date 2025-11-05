package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EjercicioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,
        String instrucciones,

        @Min(value = 0, message = "La duración debe ser mayor o igual a 0")
        Integer duracionMinutos,

        @Min(value = 0, message = "Las calorías deben ser mayor o igual a 0")
        Integer caloriasQuemadas,

        String videoUrl
) {}