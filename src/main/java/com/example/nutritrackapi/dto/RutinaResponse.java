package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ejercicio;
import com.example.nutritrackapi.model.Rutina;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO de respuesta para rutinas de ejercicio
 * US-11: Crear Meta del Catálogo
 * US-13: Ver Catálogo de Metas
 * US-17: Ver Detalle de Meta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de una rutina de ejercicio")
public class RutinaResponse {

    @Schema(description = "ID de la rutina", example = "1")
    private Long id;

    @Schema(description = "Nombre de la rutina", example = "Rutina Hipertrofia 12 Semanas")
    private String nombre;

    @Schema(description = "Descripción de la rutina")
    private String descripcion;

    @Schema(description = "Duración en semanas", example = "12")
    private Integer duracionSemanas;

    @Schema(description = "Patrón de semanas que se repite", example = "2")
    private Integer patronSemanas;

    @Schema(description = "Nivel de dificultad", example = "INTERMEDIO")
    private Ejercicio.NivelDificultad nivelDificultad;

    @Schema(description = "Si la rutina está activa (RN28: soft delete)", example = "true")
    private Boolean activo;

    @Schema(description = "Etiquetas de la rutina")
    private Set<EtiquetaResponse> etiquetas;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    @Schema(description = "Número total de ejercicios programados")
    private Integer totalEjerciciosProgramados;

    /**
     * Convierte una entidad Rutina a DTO
     */
    public static RutinaResponse fromEntity(Rutina rutina) {
        RutinaResponseBuilder builder = RutinaResponse.builder()
                .id(rutina.getId())
                .nombre(rutina.getNombre())
                .descripcion(rutina.getDescripcion())
                .duracionSemanas(rutina.getDuracionSemanas())
                .patronSemanas(rutina.getPatronSemanas())
                .nivelDificultad(rutina.getNivelDificultad())
                .activo(rutina.getActivo())
                .etiquetas(rutina.getEtiquetas().stream()
                        .map(EtiquetaResponse::fromEntity)
                        .collect(Collectors.toSet()))
                .createdAt(rutina.getCreatedAt())
                .updatedAt(rutina.getUpdatedAt());

        // Contar ejercicios programados
        if (rutina.getEjercicios() != null) {
            builder.totalEjerciciosProgramados(rutina.getEjercicios().size());
        }

        return builder.build();
    }
}
