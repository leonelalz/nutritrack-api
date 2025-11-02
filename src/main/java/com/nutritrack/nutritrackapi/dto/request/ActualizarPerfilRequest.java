package com.nutritrack.nutritrackapi.dto.request;

import com.nutritrack.nutritrackapi.model.enums.NivelActividad;
import com.nutritrack.nutritrackapi.model.enums.ObjetivoGeneral;
import com.nutritrack.nutritrackapi.model.enums.UnidadesMedida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
    private String nombre;

    private UnidadesMedida unidadesMedida;

    private ObjetivoGeneral objetivoActual;

    private NivelActividad nivelActividadActual;

    private Set<Long> etiquetasSaludIds; // IDs de etiquetas (alergias, condiciones médicas)
}
