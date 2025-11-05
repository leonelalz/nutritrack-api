package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.UsuarioPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta para asignación de plan a usuario.
 * Módulo 4: US-18, US-19, US-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPlanResponse {
    
    private Long id;
    private Long planId;
    private String planNombre;
    private String planDescripcion;
    private Integer planDuracionDias;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diaActual;
    private String estado;
    private String notas;

    public static UsuarioPlanResponse fromEntity(UsuarioPlan usuarioPlan) {
        UsuarioPlanResponse response = new UsuarioPlanResponse();
        response.setId(usuarioPlan.getId());
        response.setPlanId(usuarioPlan.getPlan().getId());
        response.setPlanNombre(usuarioPlan.getPlan().getNombre());
        response.setPlanDescripcion(usuarioPlan.getPlan().getDescripcion());
        response.setPlanDuracionDias(usuarioPlan.getPlan().getDuracionDias());
        response.setFechaInicio(usuarioPlan.getFechaInicio());
        response.setFechaFin(usuarioPlan.getFechaFin());
        response.setDiaActual(usuarioPlan.getDiaActual());
        response.setEstado(usuarioPlan.getEstado().name());
        response.setNotas(usuarioPlan.getNotas());
        return response;
    }
}
