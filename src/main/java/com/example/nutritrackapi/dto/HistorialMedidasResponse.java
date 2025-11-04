package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta con medición corporal
 * Incluye conversión a la unidad preferida del usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedidasResponse {

    private Long id;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal imc;
    private LocalDate fechaMedicion;
    private PerfilUsuario.UnidadesMedida unidadPeso; // Unidad en la que se muestra el peso
}
