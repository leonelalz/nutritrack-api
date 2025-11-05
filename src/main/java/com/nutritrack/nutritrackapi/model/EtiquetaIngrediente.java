package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etiqueta_ingrediente",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_ingrediente", "id_etiqueta"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiquetaIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_ingrediente", nullable = false)
    private Long idIngrediente;

    @Column(name = "id_etiqueta", nullable = false)
    private Long idEtiqueta;
}