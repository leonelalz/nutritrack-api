package com.nutritrack.nutritrackapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEjercicioResponse {
    private Long id;
    private Long perfilUsuarioId;
    private String nombreUsuario;
    private Long ejercicioId;
    private String nombreEjercicio;
    private Long usuarioRutinaId;
    private String nombreRutina;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer seriesRealizadas;
    private Integer repeticionesRealizadas;
    private BigDecimal pesoUtilizado;
    private Integer duracionMinutos;
    private String notas;
    private BigDecimal caloriasQuemadas;
    private LocalDateTime createdAt;
}
