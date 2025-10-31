package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;
import com.nutritrack.nutritrackapi.model.enums.*;

@Entity
@Table(name = "usuario_perfil_salud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPerfilSalud {

    @Id
    @Column(name = "id_perfil")
    private UUID idperfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo_actual", nullable = false)
    private ObjetivoGeneral objetivoActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_actividad_actual", nullable = false)
    private NivelActividad nivelActividadActual;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "condiciones_medicas", columnDefinition = "TEXT")
    private String condicionesMedicas;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDate fechaActualizacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", referencedColumnName = "id", insertable = false, updatable = false)
    private PerfilUsuario perfil;
}
