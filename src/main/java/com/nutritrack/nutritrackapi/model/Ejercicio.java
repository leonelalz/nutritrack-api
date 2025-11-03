package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ejercicio", length = 50)
    private TipoEjercicio tipoEjercicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "musculo_principal", length = 100)
    private MusculoPrincipal musculoPrincipal;

    @Column
    private Integer duracion; // Duración estimada en minutos

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Dificultad dificultad;

    @Column(name = "calorias_estimadas", precision = 6, scale = 2)
    private BigDecimal caloriasEstimadas;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación N-N con Etiquetas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "etiquetas_ejercicios",
        joinColumns = @JoinColumn(name = "id_ejercicio"),
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
