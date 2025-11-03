package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_alimenticio", length = 50)
    private GrupoAlimenticio grupoAlimenticio;

    // Valores nutricionales por 100g
    @Column(precision = 5, scale = 2)
    private BigDecimal energia; // kcal

    @Column(precision = 5, scale = 2)
    private BigDecimal proteinas; // g

    @Column(precision = 5, scale = 2)
    private BigDecimal grasas; // g

    @Column(precision = 5, scale = 2)
    private BigDecimal carbohidratos; // g

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación N-N con Etiquetas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "etiquetas_ingredientes",
        joinColumns = @JoinColumn(name = "id_ingrediente"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
