package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "catalogo_meta")

public class CatalogoMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "duracion_estimada_dias")
    private Integer duracionEstimadaDias;

    // Relación con catalogo_actividad (1:N)
    @OneToMany(mappedBy = "catalogoMeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CatalogoActividad> catalogoActividades;

    // Relación con usuario_metas_asignadas (1:N)
    @OneToMany(mappedBy = "catalogoMeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioMetaAsignada> metasAsignadas;
}
