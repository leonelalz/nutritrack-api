package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar un tipo de comida
 * Permite gestionar dinámicamente los tipos de comida del sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un tipo de comida")
public class TipoComidaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Schema(description = "Nombre del tipo de comida (único)", example = "MERIENDA", required = true)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Schema(description = "Descripción opcional del tipo de comida", example = "Comida ligera entre comidas principales")
    private String descripcion;

    @Schema(description = "Orden de visualización (menor = primero)", example = "4")
    private Integer ordenVisualizacion;

    @Schema(description = "Si el tipo está activo", example = "true")
    private Boolean activo;
}
