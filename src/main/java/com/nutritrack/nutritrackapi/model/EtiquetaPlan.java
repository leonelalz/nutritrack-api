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

    @Column(name = "id_catalogo_plan", nullable = false)
    private Long idCatalogoPlan;

    @Column(name = "id_etiqueta", nullable = false)
    private Long idEtiqueta;
}