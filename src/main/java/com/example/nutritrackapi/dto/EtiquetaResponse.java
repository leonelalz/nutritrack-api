package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Etiqueta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para etiquetas
 * US-06: Gestionar Etiquetas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una etiqueta del sistema")
public class EtiquetaResponse {

    @Schema(description = "ID de la etiqueta", example = "1")
    private Long id;

    @Schema(description = "Nombre de la etiqueta", example = "Gluten")
    private String nombre;

    @Schema(description = "Tipo de etiqueta", example = "ALERGIA")
    private Etiqueta.TipoEtiqueta tipoEtiqueta;

    @Schema(description = "Descripción de la etiqueta", example = "Alergia al gluten presente en trigo, cebada y centeno")
    private String descripcion;

    @Schema(description = "Fecha de creación", example = "2025-11-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2025-11-04T15:45:00")
    private LocalDateTime updatedAt;

    /**
     * Convierte una entidad Etiqueta a DTO
     */
    public static EtiquetaResponse fromEntity(Etiqueta etiqueta) {
        return EtiquetaResponse.builder()
                .id(etiqueta.getId())
                .nombre(etiqueta.getNombre())
                .tipoEtiqueta(etiqueta.getTipoEtiqueta())
                .descripcion(etiqueta.getDescripcion())
                .createdAt(etiqueta.getCreatedAt())
                .updatedAt(etiqueta.getUpdatedAt())
                .build();
    }
}
