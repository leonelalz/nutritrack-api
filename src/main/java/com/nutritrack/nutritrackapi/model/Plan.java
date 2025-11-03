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
@Table(name = "planes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer duracionDias; // Duración total del plan en días

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToMany
    @JoinTable(
            name = "plan_etiquetas",
            joinColumns = @JoinColumn(name = "id_plan"),
            inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private List<Etiqueta> etiquetas = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlanDia> planDias = new ArrayList<>();

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlanObjetivo objetivo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void agregarDia(PlanDia planDia) {
        planDias.add(planDia);
        planDia.setPlan(this);
    }

    public void removerDia(PlanDia planDia) {
        planDias.remove(planDia);
        planDia.setPlan(null);
    }

    public void agregarEtiqueta(Etiqueta etiqueta) {
        etiquetas.add(etiqueta);
    }

    public void removerEtiqueta(Etiqueta etiqueta) {
        etiquetas.remove(etiqueta);
    }
}
