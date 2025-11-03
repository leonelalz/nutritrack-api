package com.nutritrack.nutritrackapi.dto.response;

import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRutinaResponse {
    private Long id;
    private Long perfilUsuarioId;
    private String nombreUsuario;
    private Long rutinaId;
    private String nombreRutina;
    private Integer duracionSemanasRutina;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoAsignacion estado;
    private Integer semanaActual;
    private String notas;
    private LocalDateTime createdAt;
    
    // Estadísticas
    private BigDecimal progreso; // Porcentaje (0-100)
    private Integer semanasCompletadas;
    private Integer semanasRestantes;
}
