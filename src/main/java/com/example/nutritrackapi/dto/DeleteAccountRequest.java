package com.example.nutritrackapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para confirmar eliminación de cuenta
 * US-05: Eliminar Cuenta
 * RN05: Requiere confirmación explícita escribiendo "ELIMINAR"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Confirmación para eliminar cuenta (requiere escribir 'ELIMINAR')")
public class DeleteAccountRequest {

    @NotBlank(message = "La confirmación es obligatoria")
    @Pattern(regexp = "ELIMINAR", message = "Debes escribir exactamente 'ELIMINAR' para confirmar")
    @Schema(description = "Debe escribir exactamente ELIMINAR para confirmar", example = "ELIMINAR")
    private String confirmacion;
}
