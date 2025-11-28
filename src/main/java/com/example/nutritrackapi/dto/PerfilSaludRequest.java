package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.UsuarioPerfilSalud;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para actualizar perfil de salud del usuario
 * US-04: Editar Perfil de Salud
 * RN04: Usar etiquetas maestras
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Perfil de salud del usuario (objetivos, actividad, restricciones)")
public class PerfilSaludRequest {


    @Schema(description = "Objetivo principal de salud", example = "PERDER_PESO", allowableValues = {"PERDER_PESO", "MANTENER_PESO", "GANAR_MASA_MUSCULAR", "MEJORAR_SALUD"})
    private UsuarioPerfilSalud.ObjetivoSalud objetivoActual;


    @Schema(description = "Nivel de actividad física semanal", example = "MODERADO", allowableValues = {"SEDENTARIO", "LIGERO", "MODERADO", "ACTIVO", "MUY_ACTIVO"})
    private UsuarioPerfilSalud.NivelActividad nivelActividadActual;

    /**
     * IDs de etiquetas para alergias y condiciones médicas
     * Se valida que existan en la tabla etiquetas
     */
    @Schema(description = "IDs de etiquetas para alergias y restricciones médicas", example = "[1, 5, 12]")
    private List<Long> etiquetasId;
}
