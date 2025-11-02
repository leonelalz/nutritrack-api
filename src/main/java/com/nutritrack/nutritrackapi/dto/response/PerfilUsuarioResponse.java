package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.NivelActividad;
import com.nutritrack.nutritrackapi.model.enums.ObjetivoGeneral;
import com.nutritrack.nutritrackapi.model.enums.UnidadesMedida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuarioResponse {

    private UUID profileId;
    private String nombre;
    private UnidadesMedida unidadesMedida;
    private LocalDate fechaInicioApp;
    
    // Perfil de Salud
    private PerfilSaludDTO perfilSalud;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerfilSaludDTO {
        private ObjetivoGeneral objetivoActual;
        private NivelActividad nivelActividadActual;
        private Set<EtiquetaSaludDTO> etiquetasSalud;
        private LocalDate fechaActualizacion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EtiquetaSaludDTO {
        private Long id;
        private String nombre;
        private String tipoEtiqueta;
    }
}
