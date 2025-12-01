package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad PlanDia - Actividades diarias programadas del plan
 * Define QUÉ COMER CADA DÍA del plan nutricional
 * 
 * US-12: Gestionar Meta (configurar días)
 * US-21: Ver Actividades de mi Plan (cliente consulta)
 * 
 * MIGRACIÓN: tipoComida ahora es una relación @ManyToOne a TipoComidaEntity
 * para permitir tipos de comida dinámicos.
 * 
 * Ejemplo: Plan de 30 días con 4 comidas diarias = 120 registros en plan_dias
 */
@Entity
@Table(name = "plan_dias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Día del plan (1, 2, 3... hasta duracion_dias)
     */
    @Column(name = "numero_dia", nullable = false)
    private Integer numeroDia;

    /**
     * Tipo de comida - Relación con tabla maestra tipos_comida
     * Permite gestionar tipos de comida dinámicamente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_comida", nullable = false)
    private TipoComidaEntity tipoComida;

    /**
     * Notas adicionales para esta comida del día
     */
    @Column(length = 500)
    private String notas;

    /**
     * Relación many-to-1 con Comida
     * Qué comida del catálogo se debe consumir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    /**
     * Relación many-to-1 con Plan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @Override
    public String toString() {
        return "PlanDia{id=" + id + 
               ", dia=" + numeroDia + 
               ", tipoComida=" + (tipoComida != null ? tipoComida.getNombre() : "null") + 
               ", comida=" + (comida != null ? comida.getNombre() : "null") + "}";
    }
}
