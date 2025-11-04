package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Nueva medición corporal (peso, altura, fecha)")
public class HistorialMedidasRequest {

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "20.0", message = "El peso debe ser mayor a 20 kg")
    @DecimalMax(value = "600.0", message = "El peso debe ser menor a 600 kg")
    @Digits(integer = 3, fraction = 2, message = "El peso debe tener máximo 3 enteros y 2 decimales")
    @Schema(description = "Peso corporal (20-600 kg o LBS según unidadPeso)", example = "75.5")
    private BigDecimal peso;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "50.0", message = "La altura debe ser mayor a 50 cm")
    @DecimalMax(value = "300.0", message = "La altura debe ser menor a 300 cm")
    @Digits(integer = 3, fraction = 2, message = "La altura debe tener máximo 3 enteros y 2 decimales")
    @Schema(description = "Altura en centímetros (50-300 cm)", example = "175.0")
    private BigDecimal altura;

    @NotNull(message = "La fecha de medición es obligatoria")
    @PastOrPresent(message = "La fecha de medición no puede ser futura")
    @Schema(description = "Fecha de la medición (no puede ser futura)", example = "2025-11-04")
    private LocalDate fechaMedicion;

    /**
     * Unidad en la que el usuario envía el peso (KG o LBS)
     * Si no se especifica, se asume KG
     */
    @Schema(description = "Unidad del peso (opcional, por defecto KG)", example = "KG", allowableValues = {"KG", "LBS"})
    private PerfilUsuario.UnidadesMedida unidadPeso;
}
