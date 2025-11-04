package com.example.nutritrackapi.dto;

import com.example.nutritrackapi.model.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar unidades de medida del usuario
 * US-03: Configurar Unidades de Medida
 * RN03: Unidad aplica a todas las vistas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Configuración de unidades de medida del usuario")
public class UpdateUnidadesMedidaRequest {

    @NotNull(message = "La unidad de medida es obligatoria")
    @Schema(description = "Sistema de unidades (KG para métrico, LBS para imperial)", example = "KG", allowableValues = {"KG", "LBS"})
    private PerfilUsuario.UnidadesMedida unidadesMedida;
}
