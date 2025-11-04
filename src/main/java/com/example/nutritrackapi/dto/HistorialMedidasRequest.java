package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.PerfilUsuario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para registrar nueva medición corporal
 * US-24: Registrar y Ver Mediciones
 * RN22: Validar mediciones numéricas en rango
 * RN27: Almacenar en KG/CM, convertir en presentación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedidasRequest {

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "20.0", message = "El peso debe ser mayor a 20 kg")
    @DecimalMax(value = "600.0", message = "El peso debe ser menor a 600 kg")
    @Digits(integer = 3, fraction = 2, message = "El peso debe tener máximo 3 enteros y 2 decimales")
    private BigDecimal peso;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "50.0", message = "La altura debe ser mayor a 50 cm")
    @DecimalMax(value = "300.0", message = "La altura debe ser menor a 300 cm")
    @Digits(integer = 3, fraction = 2, message = "La altura debe tener máximo 3 enteros y 2 decimales")
    private BigDecimal altura;

    @NotNull(message = "La fecha de medición es obligatoria")
    @PastOrPresent(message = "La fecha de medición no puede ser futura")
    private LocalDate fechaMedicion;

    /**
     * Unidad en la que el usuario envía el peso (KG o LBS)
     * Si no se especifica, se asume KG
     */
    private PerfilUsuario.UnidadesMedida unidadPeso;
}
