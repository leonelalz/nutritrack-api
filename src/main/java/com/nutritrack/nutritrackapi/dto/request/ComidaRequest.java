package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComidaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String nombre,

        String descripcion,

        @Size(max = 50, message = "El tipo de comida no puede exceder 50 caracteres")
        String tipoComida,

        @Min(value = 0, message = "El tiempo de preparación debe ser mayor o igual a 0")
        Integer tiempoPreparacion,

        String instruccionesPreparacion,

        @Min(value = 1, message = "Las porciones deben ser al menos 1")
        Integer porciones,

        String imagenUrl
) {}