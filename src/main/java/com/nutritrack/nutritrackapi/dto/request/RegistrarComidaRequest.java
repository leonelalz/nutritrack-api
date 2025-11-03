package com.nutritrack.nutritrackapi.dto.request;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
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
public class RegistrarComidaRequest {

    @NotNull(message = "El ID de la comida es obligatorio")
    private Long comidaId;

    private Long usuarioPlanId; // Opcional

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotNull(message = "El tipo de comida es obligatorio")
    private TipoComida tipoComida;

    @NotNull(message = "Las porciones son obligatorias")
    @DecimalMin(value = "0.1", message = "Las porciones deben ser al menos 0.1")
    @DecimalMax(value = "10.0", message = "Las porciones no pueden exceder 10.0")
    @Digits(integer = 2, fraction = 2, message = "Las porciones deben tener máximo 2 enteros y 2 decimales")
    private BigDecimal porciones;

    private String notas;
}
