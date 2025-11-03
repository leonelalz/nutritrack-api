package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class EjercicioResponse {
    private Long id;
    private String nombre;
    private TipoEjercicio tipoEjercicio;
    private MusculoPrincipal musculoPrincipal;
    private Integer duracion;
    private Dificultad dificultad;
    private BigDecimal caloriasEstimadas;
    private Set<EtiquetaSimpleResponse> etiquetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    public static class EtiquetaSimpleResponse {
        private Long id;
        private String nombre;
    }
}
