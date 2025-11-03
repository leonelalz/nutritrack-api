package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer duracionSemanas; // Duración del programa en semanas

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToMany
    @JoinTable(
            name = "rutina_etiquetas",
            joinColumns = @JoinColumn(name = "id_rutina"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private List<Etiqueta> etiquetas = new ArrayList<>();

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RutinaEjercicio> ejercicios = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void agregarEjercicio(RutinaEjercicio rutinaEjercicio) {
        ejercicios.add(rutinaEjercicio);
        rutinaEjercicio.setRutina(this);
    }

    public void removerEjercicio(RutinaEjercicio rutinaEjercicio) {
        ejercicios.remove(rutinaEjercicio);
        rutinaEjercicio.setRutina(null);
    }

    public void agregarEtiqueta(Etiqueta etiqueta) {
        etiquetas.add(etiqueta);
    }

    public void removerEtiqueta(Etiqueta etiqueta) {
        etiquetas.remove(etiqueta);
    }
}
