package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.RegistroComida;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de request para registrar una comida.
 * Módulo 5: US-22 - Marcar Actividad como Completada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroComidaRequest {

    @NotNull(message = "El ID de la comida es obligatorio")
    private Long comidaId;

    private Long usuarioPlanId; // Opcional: si es parte de un plan activo

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private LocalTime hora; // Opcional, se usa hora actual si no se proporciona

    @NotNull(message = "El tipo de comida es obligatorio")
    private RegistroComida.TipoComida tipoComida;

    @Positive(message = "Las porciones deben ser un número positivo")
    private BigDecimal porciones;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
