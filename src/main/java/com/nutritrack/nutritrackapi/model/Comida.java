package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comidas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", length = 50)
    private TipoComida tipoComida;

    @Column(name = "tiempo_elaboracion")
    private Integer tiempoElaboracion; // Tiempo en minutos

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación N-N con Ingredientes (Receta)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "recetas",
        joinColumns = @JoinColumn(name = "id_comida"),
        inverseJoinColumns = @JoinColumn(name = "id_ingrediente")
    )
    @Builder.Default
    private Set<Ingrediente> ingredientes = new HashSet<>();

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
