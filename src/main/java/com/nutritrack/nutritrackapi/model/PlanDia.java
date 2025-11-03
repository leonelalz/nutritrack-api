package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_dias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private Integer numeroDia; // Día del plan (1 a N)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComida tipoComida; // DESAYUNO, ALMUERZO, CENA, SNACK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    @Column(length = 500)
    private String notas; // Notas adicionales para este día

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
