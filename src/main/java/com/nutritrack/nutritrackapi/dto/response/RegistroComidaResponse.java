package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
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
public class RegistroComidaResponse {
    private Long id;
    private Long perfilUsuarioId;
    private String nombreUsuario;
    private Long comidaId;
    private String nombreComida;
    private Long usuarioPlanId;
    private String nombrePlan;
    private LocalDate fecha;
    private LocalTime hora;
    private TipoComida tipoComida;
    private BigDecimal porciones;
    private String notas;
    private BigDecimal caloriasConsumidas;
    private LocalDateTime createdAt;
}
