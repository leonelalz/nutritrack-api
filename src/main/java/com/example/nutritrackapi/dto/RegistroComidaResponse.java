package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.RegistroComida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de respuesta para un registro de comida.
 * Módulo 5: US-21, US-22, US-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroComidaResponse {

    private Long id;
    private Long comidaId;
    private String comidaNombre;
    private Long usuarioPlanId;
    private LocalDate fecha;
    private LocalTime hora;
    private RegistroComida.TipoComida tipoComida;
    private BigDecimal porciones;
    private BigDecimal caloriasConsumidas;
    private String notas;

    public static RegistroComidaResponse fromEntity(RegistroComida registro) {
        return RegistroComidaResponse.builder()
                .id(registro.getId())
                .comidaId(registro.getComida().getId())
                .comidaNombre(registro.getComida().getNombre())
                .usuarioPlanId(registro.getUsuarioPlan() != null ? registro.getUsuarioPlan().getId() : null)
                .fecha(registro.getFecha())
                .hora(registro.getHora())
                .tipoComida(registro.getTipoComida())
                .porciones(registro.getPorciones())
                .caloriasConsumidas(registro.getCaloriasConsumidas())
                .notas(registro.getNotas())
                .build();
    }
}
