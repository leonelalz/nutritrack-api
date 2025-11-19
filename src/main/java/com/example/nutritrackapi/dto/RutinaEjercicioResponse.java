package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.Ejercicio;
import com.example.nutritrackapi.model.RutinaEjercicio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para ejercicios de una rutina
 * US-12: Gestionar Meta
 * US-15: Ensamblar Rutinas
 * US-17: Ver Detalle de Meta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ejercicio programado en una rutina con su configuración")
public class RutinaEjercicioResponse {

    @Schema(description = "ID del registro", example = "1")
    private Long id;

    @Schema(description = "Semana base del patrón", example = "1")
    private Integer semanaBase;

    @Schema(description = "Día de la semana (1=Lunes, 7=Domingo)", example = "1")
    private Integer diaSemana;

    @Schema(description = "Orden de ejecución dentro del día", example = "1")
    private Integer orden;

    @Schema(description = "Número de series", example = "4")
    private Integer series;

    @Schema(description = "Repeticiones por serie", example = "12")
    private Integer repeticiones;

    @Schema(description = "Peso sugerido (kg)", example = "60.0")
    private BigDecimal peso;

    @Schema(description = "Duración en minutos", example = "15")
    private Integer duracionMinutos;

    @Schema(description = "Descanso entre series (segundos)", example = "90")
    private Integer descansoSegundos;

    @Schema(description = "Notas adicionales", example = "Bajar hasta 90 grados")
    private String notas;

    @Schema(description = "Información básica del ejercicio")
    private EjercicioSimpleResponse ejercicio;

    /**
     * Convierte una entidad RutinaEjercicio a DTO
     */
    public static RutinaEjercicioResponse fromEntity(RutinaEjercicio rutinaEjercicio) {
        return RutinaEjercicioResponse.builder()
                .id(rutinaEjercicio.getId())
                .semanaBase(rutinaEjercicio.getSemanaBase())
                .diaSemana(rutinaEjercicio.getDiaSemana())
                .orden(rutinaEjercicio.getOrden())
                .series(rutinaEjercicio.getSeries())
                .repeticiones(rutinaEjercicio.getRepeticiones())
                .peso(rutinaEjercicio.getPeso())
                .duracionMinutos(rutinaEjercicio.getDuracionMinutos())
                .descansoSegundos(rutinaEjercicio.getDescansoSegundos())
                .notas(rutinaEjercicio.getNotas())
                .ejercicio(EjercicioSimpleResponse.fromEntity(rutinaEjercicio.getEjercicio()))
                .build();
    }

    /**
     * DTO simplificado de ejercicio
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información básica del ejercicio")
    public static class EjercicioSimpleResponse {
        @Schema(description = "ID del ejercicio", example = "5")
        private Long id;

        @Schema(description = "Nombre del ejercicio", example = "Press Banca")
        private String nombre;

        @Schema(description = "Tipo de ejercicio", example = "FUERZA")
        private Ejercicio.TipoEjercicio tipoEjercicio;

        @Schema(description = "Grupo muscular principal", example = "PECHO")
        private Ejercicio.GrupoMuscular grupoMuscular;

        @Schema(description = "Nivel de dificultad", example = "INTERMEDIO")
        private Ejercicio.NivelDificultad nivelDificultad;

        public static EjercicioSimpleResponse fromEntity(Ejercicio ejercicio) {
            if (ejercicio == null) return null;
            return EjercicioSimpleResponse.builder()
                    .id(ejercicio.getId())
                    .nombre(ejercicio.getNombre())
                    .tipoEjercicio(ejercicio.getTipoEjercicio())
                    .grupoMuscular(ejercicio.getGrupoMuscular())
                    .nivelDificultad(ejercicio.getNivelDificultad())
                    .build();
        }
    }
}
