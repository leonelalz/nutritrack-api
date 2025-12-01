package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.TipoComidaEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para tipos de comida
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de un tipo de comida del sistema")
public class TipoComidaResponse {

    @Schema(description = "ID del tipo de comida", example = "1")
    private Long id;

    @Schema(description = "Nombre del tipo de comida", example = "DESAYUNO")
    private String nombre;

    @Schema(description = "Descripción del tipo de comida", example = "Primera comida del día")
    private String descripcion;

    @Schema(description = "Orden de visualización", example = "1")
    private Integer ordenVisualizacion;

    @Schema(description = "Si está activo", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha de creación", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2025-01-15T15:45:00")
    private LocalDateTime updatedAt;

    /**
     * Convierte una entidad TipoComidaEntity a DTO
     */
    public static TipoComidaResponse fromEntity(TipoComidaEntity entity) {
        return TipoComidaResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .ordenVisualizacion(entity.getOrdenVisualizacion())
                .activo(entity.getActivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
