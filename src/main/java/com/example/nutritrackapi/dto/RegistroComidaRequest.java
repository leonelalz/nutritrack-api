package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.RegistroComida;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Registrar una comida consumida")
public class RegistroComidaRequest {

    @NotNull(message = "El ID de la comida es obligatorio")
    @Schema(description = "ID de la comida del catálogo (Ensalada de pollo)", example = "5", required = true)
    private Long comidaId;

    @Schema(description = "ID del plan activo (opcional)", example = "1")
    private Long usuarioPlanId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha de la comida (YYYY-MM-DD)", example = "2025-11-05", required = true)
    private LocalDate fecha;

    @Schema(description = "Hora de la comida (HH:mm:ss)", example = "13:30:00")
    private LocalTime hora;

    @NotNull(message = "El tipo de comida es obligatorio")
    @Schema(description = "Tipo de comida", example = "ALMUERZO", required = true,
            allowableValues = {"DESAYUNO", "ALMUERZO", "CENA", "SNACK", "PRE_ENTRENAMIENTO", "POST_ENTRENAMIENTO", "COLACION"})
    private RegistroComida.TipoComida tipoComida;

    @Positive(message = "Las porciones deben ser un número positivo")
    @Schema(description = "Número de porciones consumidas", example = "1.0")
    private BigDecimal porciones;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Schema(description = "Notas adicionales", example = "Comida completa, muy sabrosa")
    private String notas;
}
