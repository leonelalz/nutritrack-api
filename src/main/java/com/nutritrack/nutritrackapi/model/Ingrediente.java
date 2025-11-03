package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredientes")
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

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Información nutricional (por 100g)
    @Column(precision = 8, scale = 2)
    private BigDecimal energia; // kcal

    @Column(precision = 8, scale = 2)
    private BigDecimal proteinas; // gramos

    @Column(precision = 8, scale = 2)
    private BigDecimal grasas; // gramos

    @Column(precision = 8, scale = 2)
    private BigDecimal carbohidratos; // gramos

    @Column(name = "grupo_alimenticio", length = 100)
    private String grupoAlimenticio; // "Lácteos", "Carnes", "Vegetales", etc.

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con etiquetas
    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EtiquetaIngrediente> etiquetas = new ArrayList<>();

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