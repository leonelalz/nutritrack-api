package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Comida;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO de respuesta para comidas
 * US-09: Gestionar Comidas
 * 
 * MIGRACIÓN: tipoComida ahora devuelve objeto con id y nombre
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de una comida con sus ingredientes")
public class ComidaResponse {

    @Schema(description = "ID de la comida", example = "1")
    private Long id;

    @Schema(description = "Nombre de la comida", example = "Ensalada César con pollo")
    private String nombre;

    @Schema(description = "ID del tipo de comida", example = "2")
    private Long tipoComidaId;

    @Schema(description = "Nombre del tipo de comida", example = "ALMUERZO")
    private String tipoComida;

    @Schema(description = "Descripción de la comida")
    private String descripcion;

    @Schema(description = "Tiempo de preparación en minutos", example = "20")
    private Integer tiempoPreparacionMinutos;

    @Schema(description = "Número de porciones", example = "2")
    private Integer porciones;

    @Schema(description = "Instrucciones de preparación")
    private String instrucciones;

    @Schema(description = "Ingredientes de la comida")
    private List<RecetaIngredienteResponse> ingredientes;

    @Schema(description = "Información nutricional total")
    private InformacionNutricionalResponse nutricionTotal;

    @Schema(description = "Etiquetas asociadas")
    private Set<EtiquetaResponse> etiquetas;

    @Schema(description = "Fecha de creación", example = "2025-11-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2025-11-04T15:45:00")
    private LocalDateTime updatedAt;

    /**
     * Convierte una entidad Comida a DTO
     */
    public static ComidaResponse fromEntity(Comida comida) {
        return ComidaResponse.builder()
                .id(comida.getId())
                .nombre(comida.getNombre())
                .tipoComidaId(comida.getTipoComida() != null ? comida.getTipoComida().getId() : null)
                .tipoComida(comida.getTipoComida() != null ? comida.getTipoComida().getNombre() : null)
                .descripcion(comida.getDescripcion())
                .tiempoPreparacionMinutos(comida.getTiempoPreparacionMinutos())
                .porciones(comida.getPorciones())
                .instrucciones(comida.getInstrucciones())
                .etiquetas(comida.getEtiquetas().stream()
                        .map(EtiquetaResponse::fromEntity)
                        .collect(Collectors.toSet()))
                .createdAt(comida.getCreatedAt())
                .updatedAt(comida.getUpdatedAt())
                .build();
    }

    /**
     * DTO para información nutricional total calculada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información nutricional total de la comida")
    public static class InformacionNutricionalResponse {
        @Schema(description = "Proteínas totales (g)", example = "46.5")
        private BigDecimal proteinasTotales;

        @Schema(description = "Carbohidratos totales (g)", example = "15.0")
        private BigDecimal carbohidratosTotales;

        @Schema(description = "Grasas totales (g)", example = "12.5")
        private BigDecimal grasasTotales;

        @Schema(description = "Energía total (kcal)", example = "350.0")
        private BigDecimal energiaTotal;

        @Schema(description = "Fibra total (g)", example = "2.5")
        private BigDecimal fibraTotal;

        @Schema(description = "Cantidad total de ingredientes (g)", example = "350.0")
        private BigDecimal pesoTotal;
    }
}
