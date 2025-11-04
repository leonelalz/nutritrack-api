package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los ejercicios del catálogo.
 * Contiene información sobre tipo, dificultad, grupo muscular y calorías quemadas.
 * 
 * US-08: Gestionar Ejercicios
 * RN07: Nombres únicos (unique constraint)
 * RN09: No eliminar si está en uso en rutinas
 */
@Entity
@Table(name = "ejercicios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ejercicio", length = 50, nullable = false)
    private TipoEjercicio tipoEjercicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", length = 50, nullable = false)
    private GrupoMuscular grupoMuscular;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_dificultad", length = 50, nullable = false)
    private NivelDificultad nivelDificultad;

    @Column(name = "calorias_quemadas_por_minuto", precision = 6, scale = 2)
    private BigDecimal caloriasQuemadasPorMinuto;

    @Column(name = "duracion_estimada_minutos")
    private Integer duracionEstimadaMinutos;

    @Column(name = "equipo_necesario", length = 500)
    private String equipoNecesario;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "ejercicio_etiquetas",
        joinColumns = @JoinColumn(name = "id_ejercicio"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Enum para tipo de ejercicio
     */
    public enum TipoEjercicio {
        CARDIO,
        FUERZA,
        FLEXIBILIDAD,
        EQUILIBRIO,
        HIIT,
        YOGA,
        PILATES,
        FUNCIONAL,
        DEPORTIVO,
        REHABILITACION,
        OTRO
    }

    /**
     * Enum para grupo muscular principal
     */
    public enum GrupoMuscular {
        PECHO,
        ESPALDA,
        HOMBROS,
        BRAZOS,
        BICEPS,
        TRICEPS,
        ABDOMINALES,
        PIERNAS,
        CUADRICEPS,
        ISQUIOTIBIALES,
        GLUTEOS,
        GEMELOS,
        CORE,
        CARDIO,
        CUERPO_COMPLETO,
        OTRO
    }

    /**
     * Enum para nivel de dificultad
     */
    public enum NivelDificultad {
        PRINCIPIANTE,
        INTERMEDIO,
        AVANZADO,
        EXPERTO
    }

    @Override
    public String toString() {
        return "Ejercicio{id=" + id + ", nombre='" + nombre + "', tipo=" + tipoEjercicio + "}";
    }
}
