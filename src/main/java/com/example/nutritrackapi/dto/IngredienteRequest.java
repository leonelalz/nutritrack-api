package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ingrediente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO para crear o actualizar un ingrediente
 * US-07: Gestionar Ingredientes
 * RN07: Nombre único
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un ingrediente (valores por 100g)")
public class IngredienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
    @Schema(description = "Nombre del ingrediente", example = "Pechuga de pollo", required = true)
    private String nombre;

    @NotNull(message = "Las proteínas son obligatorias")
    @DecimalMin(value = "0.0", message = "Las proteínas no pueden ser negativas")
    @DecimalMax(value = "100.0", message = "Las proteínas no pueden exceder 100g por 100g")
    @Schema(description = "Proteínas en gramos por 100g", example = "31.0", required = true)
    private BigDecimal proteinas;

    @NotNull(message = "Los carbohidratos son obligatorios")
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    @DecimalMax(value = "100.0", message = "Los carbohidratos no pueden exceder 100g por 100g")
    @Schema(description = "Carbohidratos en gramos por 100g", example = "0.0", required = true)
    private BigDecimal carbohidratos;

    @NotNull(message = "Las grasas son obligatorias")
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    @DecimalMax(value = "100.0", message = "Las grasas no pueden exceder 100g por 100g")
    @Schema(description = "Grasas en gramos por 100g", example = "3.6", required = true)
    private BigDecimal grasas;

    @NotNull(message = "La energía es obligatoria")
    @DecimalMin(value = "0.0", message = "La energía no puede ser negativa")
    @DecimalMax(value = "900.0", message = "La energía no puede exceder 900 kcal por 100g")
    @Schema(description = "Energía en kcal por 100g", example = "165.0", required = true)
    private BigDecimal energia;

    @DecimalMin(value = "0.0", message = "La fibra no puede ser negativa")
    @DecimalMax(value = "100.0", message = "La fibra no puede exceder 100g por 100g")
    @Schema(description = "Fibra en gramos por 100g", example = "0.0")
    private BigDecimal fibra;

    @NotNull(message = "La categoría de alimento es obligatoria")
    @Schema(description = "Categoría del alimento", example = "PROTEINAS", required = true,
            allowableValues = {"FRUTAS", "VERDURAS", "CEREALES", "LEGUMBRES", "PROTEINAS", "LACTEOS", 
                             "GRASAS_SALUDABLES", "AZUCARES", "BEBIDAS", "CONDIMENTOS", 
                             "FRUTOS_SECOS", "SEMILLAS", "TUBERCULOS", "OTRO"})
    private Ingrediente.CategoriaAlimento categoriaAlimento;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Schema(description = "Descripción del ingrediente", 
            example = "Pechuga de pollo sin piel, alta en proteínas y baja en grasas")
    private String descripcion;

    @Schema(description = "IDs de etiquetas a asignar", example = "[1, 2, 3]")
    private Set<Long> etiquetaIds;
}
