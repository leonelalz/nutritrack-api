package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recetas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_comida", "id_ingrediente"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ingrediente", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "cantidad_ingrediente", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadIngrediente; // en gramos

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida; // "g", "ml", "unidad", "taza", etc.
}