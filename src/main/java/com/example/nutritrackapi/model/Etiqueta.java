package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad Etiqueta - Tabla maestra de categorización del sistema
 * Soporta 7 tipos diferentes de clasificación:
 * - ALERGIA: Gluten, Lactosa, Maní
 * - CONDICION_MEDICA: Diabetes, Hipertensión, Celíaco
 * - OBJETIVO: Pérdida de peso, Ganancia muscular
 * - DIETA: Vegana, Vegetariana, Keto, Paleo
 * - DIFICULTAD: Principiante, Intermedio, Avanzado
 * - GRUPO_MUSCULAR: Pecho, Espalda, Piernas
 * - TIPO_EJERCICIO: Cardio, Fuerza, HIIT, Yoga
 * 
 * US-06: Gestionar Etiquetas
 * RN06: Nombres únicos
 * RN08: No eliminar si está en uso
 */
@Entity
@Table(name = "etiquetas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "tipo_etiqueta", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoEtiqueta tipoEtiqueta;

    @Column(length = 500)
    private String descripcion;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum de tipos de etiqueta según arquitectura del sistema
     */
    public enum TipoEtiqueta {
        ALERGIA,           // Alergias alimentarias
        CONDICION_MEDICA,  // Condiciones médicas del usuario
        OBJETIVO,          // Objetivos de salud/fitness
        DIETA,             // Tipos de dieta
        DIFICULTAD,        // Nivel de dificultad
        GRUPO_MUSCULAR,    // Grupos musculares trabajados
        TIPO_EJERCICIO     // Tipo de ejercicio
    }
}
