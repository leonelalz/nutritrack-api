package com.example.nutritrackapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de respuesta para un registro de ejercicio.
 * Módulo 5: US-21, US-22, US-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEjercicioResponse {

    private Long id;
    private Long ejercicioId;
    private String ejercicioNombre;
    private Long usuarioRutinaId;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer series;
    private Integer repeticiones;
    private BigDecimal pesoKg;
    private Integer duracionMinutos;
    private BigDecimal caloriasQuemadas;
    private String notas;

    public static RegistroEjercicioResponse fromEntity(com.example.nutritrackapi.model.RegistroEjercicio registro) {
        return RegistroEjercicioResponse.builder()
                .id(registro.getId())
                .ejercicioId(registro.getEjercicio().getId())
                .ejercicioNombre(registro.getEjercicio().getNombre())
                .usuarioRutinaId(registro.getUsuarioRutina() != null ? registro.getUsuarioRutina().getId() : null)
                .fecha(registro.getFecha())
                .hora(registro.getHora())
                .series(registro.getSeriesRealizadas())
                .repeticiones(registro.getRepeticionesRealizadas())
                .pesoKg(registro.getPesoUtilizado())
                .duracionMinutos(registro.getDuracionMinutos())
                .caloriasQuemadas(registro.getCaloriasQuemadas())
                .notas(registro.getNotas())
                .build();
    }
}
