package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ejercicio;
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
 * DTO de respuesta para ejercicios
 * US-08: Gestionar Ejercicios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un ejercicio")
public class EjercicioResponse {

    @Schema(description = "ID del ejercicio", example = "1")
    private Long id;

    @Schema(description = "Nombre del ejercicio", example = "Sentadillas")
    private String nombre;

    @Schema(description = "Descripción del ejercicio")
    private String descripcion;

    @Schema(description = "Tipo de ejercicio", example = "FUERZA")
    private Ejercicio.TipoEjercicio tipoEjercicio;

    @Schema(description = "Grupo muscular principal", example = "PIERNAS")
    private Ejercicio.GrupoMuscular grupoMuscular;

    @Schema(description = "Nivel de dificultad", example = "INTERMEDIO")
    private Ejercicio.NivelDificultad nivelDificultad;

    @Schema(description = "Calorías quemadas por minuto", example = "8.5")
    private BigDecimal caloriasQuemadasPorMinuto;

    @Schema(description = "Duración estimada en minutos", example = "15")
    private Integer duracionEstimadaMinutos;

    @Schema(description = "Equipo necesario", example = "Barra olímpica, discos de peso")
    private String equipoNecesario;

    @Schema(description = "Etiquetas asociadas")
    private Set<EtiquetaResponse> etiquetas;

    @Schema(description = "Fecha de creación", example = "2025-11-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2025-11-04T15:45:00")
    private LocalDateTime updatedAt;

    /**
     * Convierte una entidad Ejercicio a DTO
     */
    public static EjercicioResponse fromEntity(Ejercicio ejercicio) {
        return EjercicioResponse.builder()
                .id(ejercicio.getId())
                .nombre(ejercicio.getNombre())
                .descripcion(ejercicio.getDescripcion())
                .tipoEjercicio(ejercicio.getTipoEjercicio())
                .grupoMuscular(ejercicio.getGrupoMuscular())
                .nivelDificultad(ejercicio.getNivelDificultad())
                .caloriasQuemadasPorMinuto(ejercicio.getCaloriasQuemadasPorMinuto())
                .duracionEstimadaMinutos(ejercicio.getDuracionEstimadaMinutos())
                .equipoNecesario(ejercicio.getEquipoNecesario())
                .etiquetas(ejercicio.getEtiquetas().stream()
                        .map(EtiquetaResponse::fromEntity)
                        .collect(Collectors.toSet()))
                .createdAt(ejercicio.getCreatedAt())
                .updatedAt(ejercicio.getUpdatedAt())
                .build();
    }
}
