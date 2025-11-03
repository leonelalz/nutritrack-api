package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(Receta.RecetaId.class)
public class Receta {

    @Id
    @Column(name = "id_comida")
    private Long idComida;

    @Id
    @Column(name = "id_ingrediente")
    private Long idIngrediente;

    @Column(name = "cantidad_ingrediente", nullable = false, precision = 5, scale = 2)
    private BigDecimal cantidadIngrediente; // Cantidad en gramos

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", insertable = false, updatable = false)
    private Comida comida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ingrediente", insertable = false, updatable = false)
    private Ingrediente ingrediente;

    // Clase para clave compuesta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecetaId implements Serializable {
        private Long idComida;
        private Long idIngrediente;
    }
}
