package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_comida", length = 50)
    private String tipoComida; // "Desayuno", "Almuerzo", "Cena", "Snack"

    @Column(name = "tiempo_preparacion")
    private Integer tiempoPreparacion; // en minutos

    @Column(columnDefinition = "TEXT")
    private String instruccionesPreparacion;

    @Column(name = "porciones")
    private Integer porciones;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con recetas (ingredientes)
    @OneToMany(mappedBy = "comida", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Receta> recetas = new ArrayList<>();

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