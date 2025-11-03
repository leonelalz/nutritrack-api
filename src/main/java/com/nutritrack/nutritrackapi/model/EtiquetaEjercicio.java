package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etiqueta_ejercicio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_ejercicio", "id_etiqueta"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiquetaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;  // ✅ Relación con entidad Ejercicio

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etiqueta", nullable = false)
    private Etiqueta etiqueta;
}