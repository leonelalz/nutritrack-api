package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "usuario_actividades_progreso")

public class UsuarioActividadProgreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con usuario_metas_asignadas (1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_meta_asignada", nullable = false)
    private UsuarioMetaAsignada metaAsignada;

    // Relación con catalogo_actividad (1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo_actividad", nullable = false)
    private CatalogoActividad catalogoActividad;

    @Column(name = "actividad_acabada", nullable = false)
    private boolean actividadAcabada;

    @Column(name = "fecha_completado")
    private LocalDate fechaCompletado;
}
