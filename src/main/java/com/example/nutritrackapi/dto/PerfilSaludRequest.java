package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.UsuarioPerfilSalud;
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
public class PerfilSaludRequest {

    @NotNull(message = "El objetivo de salud es obligatorio")
    private UsuarioPerfilSalud.ObjetivoSalud objetivoActual;

    @NotNull(message = "El nivel de actividad es obligatorio")
    private UsuarioPerfilSalud.NivelActividad nivelActividadActual;

    /**
     * IDs de etiquetas para alergias y condiciones médicas
     * Se valida que existan en la tabla etiquetas
     */
    private List<Long> etiquetasId;
}
