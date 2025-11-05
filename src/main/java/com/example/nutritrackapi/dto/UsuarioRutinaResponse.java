package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.UsuarioRutina;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta para asignación de rutina a usuario.
 * Módulo 4: US-18, US-19, US-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRutinaResponse {
    
    private Long id;
    private Long rutinaId;
    private String rutinaNombre;
    private String rutinaDescripcion;
    private Integer rutinaDuracionSemanas;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer semanaActual;
    private String estado;
    private String notas;

    public static UsuarioRutinaResponse fromEntity(UsuarioRutina usuarioRutina) {
        UsuarioRutinaResponse response = new UsuarioRutinaResponse();
        response.setId(usuarioRutina.getId());
        response.setRutinaId(usuarioRutina.getRutina().getId());
        response.setRutinaNombre(usuarioRutina.getRutina().getNombre());
        response.setRutinaDescripcion(usuarioRutina.getRutina().getDescripcion());
        response.setRutinaDuracionSemanas(usuarioRutina.getRutina().getDuracionSemanas());
        response.setFechaInicio(usuarioRutina.getFechaInicio());
        response.setFechaFin(usuarioRutina.getFechaFin());
        response.setSemanaActual(usuarioRutina.getSemanaActual());
        response.setEstado(usuarioRutina.getEstado().name());
        response.setNotas(usuarioRutina.getNotas());
        return response;
    }
}
