package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ingrediente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO de respuesta para ingredientes
 * US-07: Gestionar Ingredientes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información nutricional de un ingrediente (por 100g)")
public class IngredienteResponse {

    @Schema(description = "ID del ingrediente", example = "1")
    private Long id;

    @Schema(description = "Nombre del ingrediente", example = "Pechuga de pollo")
    private String nombre;

    @Schema(description = "Proteínas en gramos por 100g", example = "31.0")
    private BigDecimal proteinas;

    @Schema(description = "Carbohidratos en gramos por 100g", example = "0.0")
    private BigDecimal carbohidratos;

    @Schema(description = "Grasas en gramos por 100g", example = "3.6")
    private BigDecimal grasas;

    @Schema(description = "Energía en kcal por 100g", example = "165.0")
    private BigDecimal energia;

    @Schema(description = "Fibra en gramos por 100g", example = "0.0")
    private BigDecimal fibra;

    @Schema(description = "Categoría del alimento", example = "PROTEINAS")
    private Ingrediente.CategoriaAlimento categoriaAlimento;

    @Schema(description = "Descripción del ingrediente")
    private String descripcion;

    @Schema(description = "Etiquetas asociadas")
    private Set<EtiquetaResponse> etiquetas;

    @Schema(description = "Fecha de creación", example = "2025-11-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2025-11-04T15:45:00")
    private LocalDateTime updatedAt;

    /**
     * Convierte una entidad Ingrediente a DTO
     */
    public static IngredienteResponse fromEntity(Ingrediente ingrediente) {
        return IngredienteResponse.builder()
                .id(ingrediente.getId())
                .nombre(ingrediente.getNombre())
                .proteinas(ingrediente.getProteinas())
                .carbohidratos(ingrediente.getCarbohidratos())
                .grasas(ingrediente.getGrasas())
                .energia(ingrediente.getEnergia())
                .fibra(ingrediente.getFibra())
                .categoriaAlimento(ingrediente.getCategoriaAlimento())
                .descripcion(ingrediente.getDescripcion())
                .etiquetas(ingrediente.getEtiquetas().stream()
                        .map(EtiquetaResponse::fromEntity)
                        .collect(Collectors.toSet()))
                .createdAt(ingrediente.getCreatedAt())
                .updatedAt(ingrediente.getUpdatedAt())
                .build();
    }
}
