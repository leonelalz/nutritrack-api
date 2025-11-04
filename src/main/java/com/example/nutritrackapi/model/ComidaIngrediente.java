package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad intermedia para la relación many-to-many entre Comida e Ingrediente.
 * Representa los ingredientes de una comida con sus cantidades específicas.
 * 
 * US-10: Gestionar Recetas
 * RN10: Cantidad debe ser numérica positiva
 */
@Entity
@Table(name = "comida_ingredientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComidaIngrediente {

    @EmbeddedId
    private ComidaIngredienteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("comidaId")
    @JoinColumn(name = "id_comida")
    private Comida comida;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredienteId")
    @JoinColumn(name = "id_ingrediente")
    private Ingrediente ingrediente;

    /**
     * Cantidad del ingrediente en gramos
     * RN10: Debe ser positiva y no nula
     */
    @Column(name = "cantidad_gramos", nullable = false, precision = 7, scale = 2)
    private BigDecimal cantidadGramos;

    @Column(columnDefinition = "TEXT")
    private String notas;

    /**
     * Clase de clave compuesta
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ComidaIngredienteId implements Serializable {
        @Column(name = "id_comida")
        private Long comidaId;

        @Column(name = "id_ingrediente")
        private Long ingredienteId;
    }

    @Override
    public String toString() {
        return "ComidaIngrediente{comidaId=" + id.getComidaId() + 
               ", ingredienteId=" + id.getIngredienteId() + 
               ", cantidad=" + cantidadGramos + "g}";
    }
}
