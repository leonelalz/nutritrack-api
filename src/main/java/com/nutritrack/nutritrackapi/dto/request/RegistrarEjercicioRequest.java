package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarEjercicioRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    private Long ejercicioId;

    private Long usuarioRutinaId; // Opcional

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @Min(value = 1, message = "Las series deben ser al menos 1")
    @Max(value = 50, message = "Las series no pueden exceder 50")
    private Integer seriesRealizadas;

    @Min(value = 1, message = "Las repeticiones deben ser al menos 1")
    @Max(value = 200, message = "Las repeticiones no pueden exceder 200")
    private Integer repeticionesRealizadas;

    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
    @Digits(integer = 4, fraction = 2, message = "El peso debe tener máximo 4 enteros y 2 decimales")
    private BigDecimal pesoUtilizado;

    @NotNull(message = "La duración en minutos es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 300, message = "La duración no puede exceder 300 minutos")
    private Integer duracionMinutos;

    private String notas;
}
