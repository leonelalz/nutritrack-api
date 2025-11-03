package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.EstadoMeta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "usuario_metas_asignadas")
public class UsuarioMetaAsignada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private UUID idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo_meta", nullable = false)
    private CatalogoMeta catalogoMeta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoMeta estado;

    @Column(name = "fecha_inicio_asignacion", nullable = false)
    private LocalDate fechaInicioAsignacion;

    @Column(name = "fecha_fin_asignacion")
    private LocalDate fechaFinAsignacion;
}
