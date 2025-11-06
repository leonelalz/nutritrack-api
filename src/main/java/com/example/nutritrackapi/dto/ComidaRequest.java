package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Comida;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para crear o actualizar una comida
 * US-09: Gestionar Comidas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar una comida/receta")
public class ComidaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
    @Schema(description = "Nombre de la comida", example = "Ensalada de pollo", required = true)
    private String nombre;

    @NotNull(message = "El tipo de comida es obligatorio")
    @Schema(description = "Tipo de comida", example = "ALMUERZO", required = true,
            allowableValues = {"DESAYUNO", "ALMUERZO", "CENA", "SNACK", "PRE_ENTRENAMIENTO", 
                             "POST_ENTRENAMIENTO", "COLACION", "MERIENDA"})
    private Comida.TipoComida tipoComida;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    @Schema(description = "Descripción de la comida", 
            example = "Ensalada fresca con lechuga romana, pollo a la plancha, crutones y aderezo César")
    private String descripcion;

    @Min(value = 1, message = "El tiempo de preparación debe ser al menos 1 minuto")
    @Max(value = 480, message = "El tiempo de preparación no puede exceder 480 minutos")
    @Schema(description = "Tiempo de preparación en minutos", example = "20")
    private Integer tiempoPreparacionMinutos;

    @Min(value = 1, message = "Las porciones deben ser al menos 1")
    @Max(value = 50, message = "Las porciones no pueden exceder 50")
    @Schema(description = "Número de porciones que rinde la receta", example = "2")
    private Integer porciones;

    @Size(max = 5000, message = "Las instrucciones no pueden exceder 5000 caracteres")
    @Schema(description = "Instrucciones de preparación paso a paso", 
            example = "1. Lavar y cortar la lechuga\n2. Cocinar el pollo a la plancha\n3. Mezclar todos los ingredientes...")
    private String instrucciones;

    @Schema(description = "IDs de etiquetas a asignar", example = "[1, 2, 3]")
    private Set<Long> etiquetaIds;
}
