package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Comida;
import com.example.nutritrackapi.model.PlanDia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para días programados del plan
 * US-12: Gestionar Meta
 * US-17: Ver Detalle de Meta
 * US-21: Ver Actividades de mi Plan
 * 
 * MIGRACIÓN: tipoComida ahora devuelve id y nombre
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Actividad diaria programada del plan")
public class PlanDiaResponse {

    @Schema(description = "ID del registro", example = "1")
    private Long id;

    @Schema(description = "Día del plan", example = "1")
    private Integer numeroDia;

    @Schema(description = "ID del tipo de comida", example = "1")
    private Long tipoComidaId;

    @Schema(description = "Nombre del tipo de comida", example = "DESAYUNO")
    private String tipoComida;

    @Schema(description = "Información básica de la comida programada")
    private ComidaSimpleResponse comida;

    @Schema(description = "Notas adicionales", example = "Tomar con agua tibia")
    private String notas;

    /**
     * Convierte una entidad PlanDia a DTO
     */
    public static PlanDiaResponse fromEntity(PlanDia planDia) {
        return PlanDiaResponse.builder()
                .id(planDia.getId())
                .numeroDia(planDia.getNumeroDia())
                .tipoComidaId(planDia.getTipoComida() != null ? planDia.getTipoComida().getId() : null)
                .tipoComida(planDia.getTipoComida() != null ? planDia.getTipoComida().getNombre() : null)
                .comida(ComidaSimpleResponse.fromEntity(planDia.getComida()))
                .notas(planDia.getNotas())
                .build();
    }

    /**
     * DTO simplificado de comida (sin ingredientes ni detalles completos)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información básica de la comida")
    public static class ComidaSimpleResponse {
        @Schema(description = "ID de la comida", example = "15")
        private Long id;

        @Schema(description = "Nombre de la comida", example = "Avena con frutas")
        private String nombre;

        @Schema(description = "ID del tipo de comida", example = "1")
        private Long tipoComidaId;

        @Schema(description = "Nombre del tipo de comida", example = "DESAYUNO")
        private String tipoComida;

        public static ComidaSimpleResponse fromEntity(Comida comida) {
            if (comida == null) return null;
            return ComidaSimpleResponse.builder()
                    .id(comida.getId())
                    .nombre(comida.getNombre())
                    .tipoComidaId(comida.getTipoComida() != null ? comida.getTipoComida().getId() : null)
                    .tipoComida(comida.getTipoComida() != null ? comida.getTipoComida().getNombre() : null)
                    .build();
        }
    }
}
