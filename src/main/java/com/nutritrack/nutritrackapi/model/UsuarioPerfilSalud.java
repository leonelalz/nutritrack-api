package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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
    private Long idPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo_actual", nullable = false)
    private ObjetivoGeneral objetivoActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_actividad_actual", nullable = false)
    private NivelActividad nivelActividadActual;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_etiquetas_salud",
        joinColumns = @JoinColumn(name = "id_perfil"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetasSalud = new HashSet<>();

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDate fechaActualizacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", referencedColumnName = "id", insertable = false, updatable = false)
    private PerfilUsuario perfil;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDate.now();
    }
}
