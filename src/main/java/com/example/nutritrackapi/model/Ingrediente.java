package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los ingredientes del catálogo.
 * Contiene información nutricional por cada 100g del ingrediente.
 * 
 * US-07: Gestionar Ingredientes
 * RN07: Nombres únicos (unique constraint)
 * RN09: No eliminar si está en uso en recetas
 */
@Entity
@Table(name = "ingredientes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal proteinas; // En gramos por 100g

    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal carbohidratos; // En gramos por 100g

    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal grasas; // En gramos por 100g

    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal energia; // En kcal por 100g

    @Column(precision = 7, scale = 2)
    private BigDecimal fibra; // En gramos por 100g

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_alimento", length = 50, nullable = false)
    private CategoriaAlimento categoriaAlimento;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "ingrediente_etiquetas",
        joinColumns = @JoinColumn(name = "id_ingrediente"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Relación 1-to-many con ComidaIngrediente (Receta)
     */
    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ComidaIngrediente> comidaIngredientes = new HashSet<>();

    /**
     * Enum para categorías de alimentos
     */
    public enum CategoriaAlimento {
        FRUTAS,
        VERDURAS,
        CEREALES,
        LEGUMBRES,
        PROTEINAS,
        LACTEOS,
        GRASAS_SALUDABLES,
        AZUCARES,
        BEBIDAS,
        CONDIMENTOS,
        FRUTOS_SECOS,
        SEMILLAS,
        TUBERCULOS,
        OTRO
    }

    @Override
    public String toString() {
        return "Ingrediente{id=" + id + ", nombre='" + nombre + "', categoria=" + categoriaAlimento + "}";
    }
}
