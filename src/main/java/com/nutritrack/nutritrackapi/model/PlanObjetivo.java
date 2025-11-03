package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_objetivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanObjetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false, unique = true)
    private Plan plan;

    @Column(precision = 10, scale = 2)
    private BigDecimal caloriasObjetivo; // Calorías diarias objetivo

    @Column(precision = 10, scale = 2)
    private BigDecimal proteinasObjetivo; // Gramos de proteínas diarias

    @Column(precision = 10, scale = 2)
    private BigDecimal grasasObjetivo; // Gramos de grasas diarias

    @Column(precision = 10, scale = 2)
    private BigDecimal carbohidratosObjetivo; // Gramos de carbohidratos diarios

    @Column(length = 500)
    private String descripcion; // Descripción del objetivo

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
