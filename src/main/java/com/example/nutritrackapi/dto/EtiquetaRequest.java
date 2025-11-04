package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Etiqueta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar una etiqueta
 * US-06: Gestionar Etiquetas
 * RN06: Nombre único
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar una etiqueta del sistema")
public class EtiquetaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre de la etiqueta (único)", example = "Sin Gluten", required = true)
    private String nombre;

    @NotNull(message = "El tipo de etiqueta es obligatorio")
    @Schema(description = "Tipo de etiqueta", example = "ALERGIA", required = true,
            allowableValues = {"ALERGIA", "CONDICION_MEDICA", "OBJETIVO", "DIETA", "DIFICULTAD", "GRUPO_MUSCULAR", "TIPO_EJERCICIO"})
    private Etiqueta.TipoEtiqueta tipoEtiqueta;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción opcional de la etiqueta", example = "Apto para personas con intolerancia o alergia al gluten")
    private String descripcion;
}
