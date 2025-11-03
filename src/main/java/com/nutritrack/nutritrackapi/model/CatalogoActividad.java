package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.TipoActividad;
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
@Table(name = "catalogo_actividad")
public class CatalogoActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con catalogo_meta (1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo_meta", nullable = false)
    private CatalogoMeta catalogoMeta;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_actividad", nullable = false, length = 20)
    private TipoActividad tipoActividad;

    // Relación con usuario_actividades_progreso (1:N)
    @OneToMany(mappedBy = "catalogoActividad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioActividadProgreso> usuarioActividadesProgreso;
}
