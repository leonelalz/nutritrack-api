package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etiqueta_plan",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_catalogo_plan", "id_etiqueta"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiquetaPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne con Plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo_plan", nullable = false)
    private CatalogoPlan plan;

    // Relación ManyToOne con Etiqueta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etiqueta", nullable = false)
    private Etiqueta etiqueta;
}