package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etiqueta_meta",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_catalogo_meta", "id_etiqueta"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiquetaMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_catalogo_meta", nullable = false)
    private Long idCatalogoMeta;

    @Column(name = "id_etiqueta", nullable = false)
    private Long idEtiqueta;
}